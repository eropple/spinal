package com.edcanhack.spinal.leader.publishers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

// TODO: refactor the Boss classes into something easier to deal with
class PublisherBoss(val config: LeaderConfiguration) extends BaseActor[LeaderConfiguration] {

  override def preStart() = {
    super.preStart()
    self ! Messages.Leader.Boss.Initialize
  }

  def receive = {
    case Messages.Leader.Boss.Initialize => {
      logger.info(s"Creating ${config.publishers.size} publishers...")
      config.publishers.view.zipWithIndex.foreach { case (p, i) => p.initializePublisher(context, config, i) }
    }
  }
}
