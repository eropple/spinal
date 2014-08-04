package com.edcanhack.spinal.leader.observers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

// TODO: refactor the Boss classes into something easier to deal with
class LeaderObserverBoss(val config: LeaderConfiguration) extends BaseActor[LeaderConfiguration] {
  def receive = {
    case Messages.Leader.Boss.Initialize => {
      logger.info(s"Creating ${config.leaderObservers.size} observers...")
      config.leaderObservers.view.zipWithIndex.foreach { case (o, i) => o.initializeLeaderObserver(context, config, i) }
    }
  }
}
