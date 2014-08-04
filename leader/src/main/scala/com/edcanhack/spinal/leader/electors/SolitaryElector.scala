package com.edcanhack.spinal.leader.electors

import akka.actor.Actor
import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.SelfLeader
import com.edcanhack.spinal.leader.config.LeaderConfiguration

/**
 * Elector used in non-HA situations. Always thinks it's the active leader.
 */
case class SolitaryElector(config: LeaderConfiguration) extends Elector {


  val behavior: Actor.Receive = { // TODO: figure out why this is sad
    case msg: Messages.Leader.Elector.StartElection => {
      if (msg.leaders != Set(SelfLeader)) {
        logger.error("I'm a SolitaryElector, but I was given a leader set with more than just me. I'm probably confused, but will try to continue.")
      }
      self ! Messages.Leader.Elector.ElectionCompleted(SelfLeader, msg.leaders)
    }
  }
  def receive = behavior orElse baseElectorBehavior
}
