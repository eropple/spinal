package com.edcanhack.spinal.commons.state

import scalaz._, Scalaz._

case class Endpoint(address: String, port: Int)

trait Routable {
  /**
   * For use in JSON interchange.
   */
  def routableType: String
}

case class HTTPRoutable(host: String, path: Option[String], sourcePort: Int, healthChecks: Set[String], endpoints: Set[Endpoint]) extends Routable {
  def routableType = "http"
}
object HTTPRoutable {
  def merge(routes: Iterable[HTTPRoutable]): Seq[HTTPRoutable] = {
    val endpoints = scala.collection.mutable.Map[(String, Option[String], Int), Set[Endpoint]]()
    val healthChecks = scala.collection.mutable.Map[(String, Option[String], Int), Set[String]]()
    routes.foreach(r => {
      val key = (r.host, r.path, r.sourcePort)
      val endpointsValue: Set[Endpoint] = endpoints.get(key).getOrElse(Set()) ++ r.endpoints
      val healthChecksValue: Set[String] = healthChecks.get(key).getOrElse(Set()) ++ r.healthChecks

      endpoints.put(key, endpointsValue)
      healthChecks.put(key, healthChecksValue)
    })


    endpoints.map( t => {
      val key = (t._1._1, t._1._2, t._1._3)
      val hc = healthChecks.get(key).getOrElse(Set())

      val path = t._1._2.map(p => (if (p.endsWith("/"))
                                     p.substring(0, p.length - 1)
                                   else
                                     p).trim)

      HTTPRoutable(key._1, path, key._3, hc, t._2)
    }).toSeq
  }
}