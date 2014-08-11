package com.edcanhack.spinal.extensions.haproxy.worker.effectors.templates

import com.edcanhack.spinal.commons.state.{HTTPRoutable, Universe}
import com.edcanhack.spinal.extensions.haproxy.Formatting
import com.edcanhack.spinal.worker.effectors.TemplateEffector.Templater

case class HaproxyTemplater() extends Templater {
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
      val value = accum.get(key).getOrElse(Seq()) :+ r

      accum.put(key, value)
    })

    accum.toMap
  }


  // TODO: investigate better templating solutions; Twirl destroys the linebreaks and makes the code unreadable.
  private def constructTemplate(httpRoutes: Map[Int, Seq[HTTPRoutable]]): String = {
    import com.edcanhack.spinal.extensions.haproxy.Formatting._

    val sb = new StringBuilder()
    var indent = 0

    def indentString(n: Int) = " " * (n * 2)
    def append(str: => String = "") = sb.append(indentString(indent)).append(str).append("\n")
    def indented(block: => Unit) = {
      indent += 1
      block
      indent -= 1
    }
    def hostAclName(r: HTTPRoutable) = s"host::${Formatting.routeName(r)}"
    def pathAclName(r: HTTPRoutable) = s"path::${Formatting.routeName(r)}"

    append("## HAPROXY CONFIG COURTESY OF SPINAL")
    append("## file bugs at https://github.com/eropple/spinal")
    append()

    append("global")
    indented {

    }
    append()

    append("defaults")
    indented {
      append("mode http")
      append("timeout connect 10000ms") // TODO: parameterize this
      append("timeout client  50000ms") // TODO: parameterize this
      append("timeout server  50000ms") // TODO: parameterize this
    }
    append()

    for(f <- httpRoutes) {
      append(s"## spinal: ${f}")
      append(s"frontend http-${f._1}")
      indented {
        append(s"bind *:${f._1}")
        append()
        for (r <- f._2) {
          val hostTest = s"hdr(host) -i ${r.host}"
          val pathTest = if (r.path.isEmpty) "" else s"path_beg ${r.path.get}"

          append(s"acl ${pad(hostAclName(r), 60)} ${hostTest}    ${pathTest}")

        }
        append()

        for (r <- f._2) {
          append(s"use_backend ${pad(routeName(r), 52)} if ${hostAclName(r)}")
        }
      }
      append()
      for (r <- f._2) {
        append(s"backend ${routeName(r)}")
        indented {
          for(hc <- r.healthChecks) {
            append(s"option httpchk      ${hc}")
          }
          for(e <- r.endpoints.zipWithIndex) {
            append(s"server server-${pad(e._2.toString, 5)} ${e._1.address}:${e._1.port}")
          }
        }
        append()
      }
      append()
    }

    sb.toString
  }
}
