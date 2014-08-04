package com.edcanhack.spinal.commons.locators

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.config.Configuration
import com.edcanhack.spinal.commons.state.Leader
import com.edcanhack.spinal.commons.util.actors.BaseActor

abstract class Locator protected() extends BaseActor[Configuration] {
  private var leaders: Set[Leader] = Set()

  def receive = {
    case Messages.Locator.Initialize => self ! Messages.Locator.Locate
    case Messages.Locator.Locate => process()
  }

  private def process() = {
    logger.debug("Entering process pass.")
    val foundLeaders = locate()
    if (leaders != foundLeaders) {
      logger.info("The set of leaders has changed; informing elector.")

      leaders = foundLeaders

      val msg = Messages.Leader.Elector.NewLeaders(leaders)
      context.actorSelection("../leader/elector") ! msg
//      context.actorSelection(Names.Worker.subscriber) ! msg
//      context.actorSelection(Names.Leader.All.leaderObservers) ! msg
//      context.actorSelection(Names.Worker.All.workerObservers) ! msg
    }
  }

  protected def locate(): Set[Leader]
}