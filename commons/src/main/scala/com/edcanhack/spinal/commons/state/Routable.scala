package com.edcanhack.spinal.commons.state

case class Endpoint(address: String, port: Int)

trait Routable {
  def sourcePort: Int
  def endpoints: Seq[Endpoint]

  def routableType: String
}

case class TCPRoutable(sourcePort: Int, endpoints: Seq[Endpoint]) extends Routable {
  def routableType = "tcp"
}
case class UDPRoutable(sourcePort: Int, endpoints: Seq[Endpoint]) extends Routable {
  def routableType = "udp"
}
case class HTTPRoutable(host: String, sourcePort: Int, endpoints: Seq[Endpoint]) extends Routable {
  def routableType = "http"
}