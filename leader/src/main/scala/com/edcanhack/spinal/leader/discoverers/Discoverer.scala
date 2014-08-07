package com.edcanhack.spinal.leader.discoverers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.{HTTPRoutable, Routable, Universe}
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

trait Discoverer extends BaseActor[LeaderConfiguration] {
  def priority: Int

  def receive = {
    case Messages.Leader.Discoverer.StartDiscovery => discover()
    case Messages.Leader.Discoverer.Report(routes: Seq[Routable]) => {
      logger.debug(s"Reporting ${routes.size} routes pre-merge.")
      val u = mergeRoutes(routes)
      logger.debug(s"Post-mrge: ${u.size} routes.")
      context.actorSelection("..") ! Messages.Leader.DiscovererBoss.DiscoveryReport(priority, u)
    }
  }

  def mergeRoutes(routes: Seq[_ <: Routable]): Universe = {
    val http: Seq[HTTPRoutable] = routes.filter(_.isInstanceOf[HTTPRoutable]).map(_.asInstanceOf[HTTPRoutable]).toSeq

    Universe(Map())
  }

  def discover()
}
