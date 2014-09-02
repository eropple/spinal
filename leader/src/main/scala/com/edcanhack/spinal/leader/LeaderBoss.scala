package com.edcanhack.spinal.leader

import akka.actor.{Kill, Props}
import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration
import com.edcanhack.spinal.leader.discoverers.DiscovererBoss
import com.edcanhack.spinal.leader.observers.LeaderObserverBoss
import com.edcanhack.spinal.leader.publishers.PublisherBoss

/**
 * Created by ed on 8/3/14.
 */
class LeaderBoss(val config: LeaderConfiguration) extends BaseActor[LeaderConfiguration] {
  private var isActive = false

  def receive = {
    case Messages.Leader.Initialize => {
      config.elector.initializeElector(context, config)
      context.actorOf(Props(classOf[LeaderObserverBoss], config), "observers")
    }
    case Messages.Leader.GoInactive => {
      if (isActive) {
        context.actorSelection("discoverers") ! Kill
        context.actorSelection("publishers") ! Kill

        isActive = false
      }
    }
    case Messages.Leader.IsActiveLeader => {
      context.actorOf(Props(classOf[DiscovererBoss], config), "discoverers")
      context.actorOf(Props(classOf[PublisherBoss], config), "publishers")

      isActive = true
    }
  }
}
