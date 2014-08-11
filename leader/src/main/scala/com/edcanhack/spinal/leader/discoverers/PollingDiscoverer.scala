package com.edcanhack.spinal.leader.discoverers

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import com.edcanhack.spinal.commons.Messages

import scala.concurrent.duration.{FiniteDuration, Duration}

/**
 * Created by ed on 8/11/14.
 */
trait PollingDiscoverer extends Discoverer {
  import context._

  def pollingFrequency: FiniteDuration

  val behavior: Actor.Receive = {
    case Messages.Leader.Discoverer.StartDiscovery => {
      logger.info(s"Setting up a poll for every ${pollingFrequency.toMillis} ms.")
      self ! Poll
    }
    case Poll => {
      discover()
      system.scheduler.scheduleOnce(pollingFrequency, self, Poll)
    }
  }
  override def receive = behavior orElse super.receive

  case object Poll
}
