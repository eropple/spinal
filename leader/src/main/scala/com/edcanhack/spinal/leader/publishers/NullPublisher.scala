package com.edcanhack.spinal.leader.publishers

import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.leader.config.LeaderConfiguration

case class NullPublisher(val config: LeaderConfiguration) extends Publisher {
  override def publish(universe: Universe): Unit = {
    logger.info("Universe published (to nowhere, because I'm null).")
  }
}
