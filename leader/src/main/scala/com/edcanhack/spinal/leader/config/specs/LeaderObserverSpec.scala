package com.edcanhack.spinal.leader.config.specs

import akka.actor.{ActorRef, ActorRefFactory}
import com.edcanhack.spinal.leader.config.LeaderConfiguration

trait LeaderObserverSpec {
  def initializeLeaderObserver(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef
}
