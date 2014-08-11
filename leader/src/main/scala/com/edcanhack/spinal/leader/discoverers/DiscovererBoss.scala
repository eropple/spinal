package com.edcanhack.spinal.leader.discoverers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.Messages.Leader.DiscovererBoss.{Initialize, DiscoveryReport}
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

/**
 * Acts as a future sequencer on any discoverers in the swarm. Owns everything under /user/leader/discoverers/minions.
 */
case class DiscovererBoss(config: LeaderConfiguration) extends BaseActor[LeaderConfiguration] {

  var reports: Map[String, DiscoveryReport] = Map()

  override def preStart() = {
    super.preStart()
    self ! Initialize
  }

  def receive = {
    case Initialize => {
      logger.info(s"Creating ${config.discoverers.size} discoverers...")
      config.discoverers.view.zipWithIndex.foreach { case (o, i) => o.initializeDiscoverer(context, config, i) }
    }
    case report: DiscoveryReport => {
      logger.debug(s"Report received from ${sender().path.toStringWithoutAddress}.")

      reports = reports.+( (sender().path.toStringWithoutAddress, report) )

      val merged = reports.values.toSeq.sortBy(r => r.weight).foldLeft(Universe.empty)( (a, c) => a.merge(c.universe) )

      context.actorSelection("../publishers/*") ! Messages.Leader.Publisher.Publish(merged)
    }
  }
}