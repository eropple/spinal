package com.edcanhack.spinal.commons.state

case class Endpoint(address: String, port: Int)

trait Routable {
  /**
   * For use in JSON interchange.
   */
  def routableType: String
}

case class HTTPRoutable(path: String, sourcePort: Int, endpoints: Seq[Endpoint]) extends Routable {
  def routableType = "http"
}