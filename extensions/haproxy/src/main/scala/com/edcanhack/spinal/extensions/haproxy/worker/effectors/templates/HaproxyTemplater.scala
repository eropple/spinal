package com.edcanhack.spinal.extensions.haproxy.worker.effectors.templates

import java.io.File

import com.edcanhack.spinal.commons.state.{HTTPRoutableOrdering, HTTPRoutable, Universe}
import com.edcanhack.spinal.extensions.haproxy.Formatting
import com.edcanhack.spinal.worker.effectors.TemplateEffector.Templater

case class HaproxyTemplater(user: Option[String] = None,
                            group: Option[String] = None,
                            chroot: Option[File] = None) extends Templater {
  /**
   * Renders out an HAProxy template that represents the requested universe.
   *
   * Roughly in order of decomposition:
   * - universe is sorted into (host, port) -> Seq[HTTPRoutable]
   * - a `frontend` is created for every (host, port) tuple
   * - a frontend `acl` and a `use-backend` directive is created for every subsequent (host, path) tuple within it
   * - a `backend` is created for every (host, path) tuple; uses all healthchecks as `httpchk` options
   *   (HAProxy will not mark a node down unless ALL health checks in the set fail)
   * - each endpoint is a `server` within that backend
   *
   * @param universe
   * @return
   */
  override def render(universe: Universe): String = {
    constructTemplate(filterForFrontends(universe.httpRoutes))
  }

  private def filterForFrontends(routes: Seq[HTTPRoutable]): Map[Int, Seq[HTTPRoutable]] = {
    val accum = scala.collection.mutable.Map[Int, Seq[HTTPRoutable]]()

    routes.foreach(r => {
      val key = r.sourcePort
      val value = accum.getOrElse(key, Seq()) :+ r

      accum.put(key, value)
    })

    accum.map (t => (t._1, t._2.sorted(HTTPRoutableOrdering))).toMap
  }


  // TODO: investigate better templating solutions; Twirl destroys the linebreaks and makes the code unreadable.
  // TODO: strip matched path segments when redirecting via URI paths.
  private def constructTemplate(httpRoutes: Map[Int, Seq[HTTPRoutable]]): String = {
    import com.edcanhack.spinal.extensions.haproxy.Formatting._

    val sb = new StringBuilder()
    var indent = 0

    def indentString(n: Int) = " " * (n * 2)
    def w(str: => String = "") = sb.append(indentString(indent)).append(str).append("\n")
    def indented(block: => Unit) = {
      indent += 1
      block
      indent -= 1
    }
    def hostAclName(r: HTTPRoutable) = s"host::${Formatting.routeName(r)}"
    def pathAclName(r: HTTPRoutable) = s"path::${Formatting.routeName(r)}"

    w("## HAPROXY CONFIG COURTESY OF SPINAL")
    w("## file bugs at https://github.com/eropple/spinal")
    w()

    w("global")
    indented {
      user.map(u => w(s"user ${u}"))
      group.map(g => w(s"group ${g}"))
      chroot.map(c => w(s"chroot ${c}"))
      w("log /dev/log	local0")
      w("log /dev/log	local1 notice")
      w("daemon")
    }
    w()

    w("defaults")
    indented {
      w("log global")
      w("mode http")
      w("option httplog")
      w("option dontlognull")
      w("timeout connect 10000ms") // TODO: parameterize this
      w("timeout client  50000ms") // TODO: parameterize this
      w("timeout server  50000ms") // TODO: parameterize this
    }
    w()

    for(f <- httpRoutes) {
      w(s"## spinal: ${f}")
      w(s"frontend http-${f._1}")
      indented {
        w(s"bind *:${f._1}")
        w()
        for (r <- f._2) {
          val hostTest = s"{ hdr(host) -i ${r.host} }"
          val pathTest = if (r.path.isEmpty) "" else s"{ path_beg ${r.path.get} }"

          //w(s"acl ${pad(hostAclName(r), 60)} ${hostTest}    ${pathTest}")
          w(s"use_backend ${pad(routeName(r), 52)} if ${hostTest} ${pathTest}")
        }
        w()

        for (r <- f._2) {
//          w(s"acl ${pad(hostAclName(r), 60)} ${hostTest}    ${pathTest}")
        }
      }
      w()
      for (r <- f._2) {
        w(s"backend ${routeName(r)}")
        indented {
          for(hc <- r.healthChecks) {
            w(s"option httpchk      ${hc}")
          }
          for(e <- r.endpoints.zipWithIndex) {
            w(s"server server-${pad(e._2.toString, 5)} ${e._1.address}:${e._1.port}")
          }
        }
        w()
      }
      w()
    }

    sb.toString
  }
}
