package com.edcanhack.spinal.leader.discoverers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

trait Discoverer extends BaseActor[LeaderConfiguration] {
  def priority: Int

  def receive = {
    case Messages.Leader.Discoverer.StartDiscovery => discover()
    case Messages.Leader.Discoverer.Report(u: Universe) => {
      logger.debug(s"Reporting ${u.size} routes.")
      context.actorSelection("..") ! Messages.Leader.DiscovererBoss.DiscoveryReport(priority, u)
    }
  }

  def discover()
}
