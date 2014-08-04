package com.edcanhack.spinal.leader.config.specs

import akka.actor.{ActorRef, ActorRefFactory, Props}
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.leader.config.LeaderConfiguration
import com.edcanhack.spinal.leader.discoverers.StaticUniverseDiscoverer

trait DiscovererSpec {
  def initializeDiscoverer(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef
}
case class StaticUniverseDiscovererSpec(priority: Int, universe: Universe = Universe.empty) extends DiscovererSpec {
  def initializeDiscoverer(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef = {
    factory.actorOf(Props(classOf[StaticUniverseDiscoverer], configuration, priority, universe),
                        s"${actorIdx}-StaticUniverseDiscoverer")
  }
}