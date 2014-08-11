package com.edcanhack.spinal.leader.discoverers

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import com.edcanhack.spinal.commons.Messages

import scala.concurrent.duration.{FiniteDuration, Duration}

/**
 * Created by ed on 8/11/14.
 */
trait PollingDiscoverer extends Discoverer {
  def pollingFrequency: FiniteDuration

  val behavior: Actor.Receive = {
    case Messages.Leader.Discoverer.StartDiscovery => {
      logger.info(s"Setting up a poll for every ${pollingFrequency.toMillis} ms.")
      context.system.scheduler.schedule(Duration(1, TimeUnit.MILLISECONDS), pollingFrequency, self, Poll)
    }
    case Poll => discover()
  }

  case object Poll
}
