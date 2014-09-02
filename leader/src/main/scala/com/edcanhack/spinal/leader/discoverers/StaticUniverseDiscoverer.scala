package com.edcanhack.spinal.leader.discoverers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.leader.config.LeaderConfiguration

case class StaticUniverseDiscoverer(config: LeaderConfiguration, priority: Int, staticUniverse: Universe = Universe.empty) extends Discoverer {
  override def discover() = {
    logger.info(s"Returning static universe of ${staticUniverse.httpRoutes.size} HTTP routes.")
    self ! Messages.Leader.Discoverer.Report(staticUniverse.httpRoutes)
  }
}