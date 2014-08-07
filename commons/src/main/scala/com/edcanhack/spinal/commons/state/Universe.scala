package com.edcanhack.spinal.commons.state

case class Universe(httpRoutes: Seq[HTTPRoutable]) {
  def merge(other: Universe) = {
    Universe(httpRoutes = HTTPRoutable.merge(httpRoutes ++ other.httpRoutes))
  }
}

object Universe {
  val empty = Universe(httpRoutes = Seq())
}