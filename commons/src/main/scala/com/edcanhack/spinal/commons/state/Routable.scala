package com.edcanhack.spinal.commons.state

import scalaz._, Scalaz._

case class Endpoint(address: String, port: Int)

trait Routable {
  /**
   * For use in JSON interchange.
   */
  def routableType: String
}

case class HTTPRoutable(host: String, path: String, sourcePort: Int, endpoints: Set[Endpoint]) extends Routable {
  def routableType = "http"
}
object HTTPRoutable {
  def merge(routes: Iterable[HTTPRoutable]): Seq[HTTPRoutable] = {
    val accum = scala.collection.mutable.Map[(String, String, Int), Set[Endpoint]]()
    routes.foreach(r => {
      val key = (r.host, r.path, r.sourcePort)
      val value: Set[Endpoint] = (accum.get( key ) match {
        case None => Set()
        case Some(current: Set[Endpoint]) => current
      }) ++ r.endpoints

      accum.put(key, value)
    })

    accum.map( t => HTTPRoutable(t._1._1, t._1._2, t._1._3, t._2) ).toSeq
  }
}