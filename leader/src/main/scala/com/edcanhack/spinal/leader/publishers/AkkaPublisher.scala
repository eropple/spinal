package com.edcanhack.spinal.leader.publishers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.leader.config.LeaderConfiguration

class AkkaPublisher(val config: LeaderConfiguration, val target: String) extends Publisher {
  def publish(universe: Universe): Unit = {
    logger.info(s"Publishing ${universe.httpRoutes.size} HTTP routes to '${self.path}/${target}'.")
    context.actorSelection(target) ! Messages.Worker.Subscriber.PublishedTo(universe)
  }
}
