package com.edcanhack.spinal.leader.discoverers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.{Endpoint, HTTPRoutable, Routable, Universe}
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

trait Discoverer extends BaseActor[LeaderConfiguration] {
  def priority: Int

  override def preStart() = {
    super.preStart()
    self ! Messages.Leader.Discoverer.StartDiscovery
  }

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    super.preRestart(reason, message)
    self ! Messages.Leader.Discoverer.StartDiscovery
  }

  def receive = {
    case Messages.Leader.Discoverer.StartDiscovery => discover()
    case Messages.Leader.Discoverer.Report(routes: Seq[Routable]) => {
      logger.debug(s"Reporting ${routes.size} routes pre-merge.")
      val u = mergeRoutes(routes)
      logger.debug(s"Post-merge: ${u.httpRoutes.size} HTTP routes.")
      context.actorSelection("..") ! Messages.Leader.DiscovererBoss.DiscoveryReport(priority, u)
    }
  }

  def mergeRoutes(routes: Seq[_ <: Routable]): Universe = {
    val http: Seq[HTTPRoutable] = {
      val hr = routes.filter(_.isInstanceOf[HTTPRoutable]).map(_.asInstanceOf[HTTPRoutable]).toSeq
      HTTPRoutable.merge(hr)
    }

    Universe(httpRoutes = http)
  }

  def discover()
}
