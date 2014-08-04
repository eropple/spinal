package com.edcanhack.spinal.leader.electors

import akka.actor.Actor
import com.edcanhack.spinal.commons.state.SelfLeader
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.commons.{Messages, SpinalException}
import com.edcanhack.spinal.leader.config.LeaderConfiguration

trait Elector extends BaseActor[LeaderConfiguration] {
  val baseElectorBehavior: Actor.Receive = {
    case msg: Messages.Leader.Elector.NewLeaders => {
      logger.info("New leaders found; shotgunning any discoverers/publishers that may be running.")

      context.actorSelection("..") ! Messages.Leader.GoInactive

      logger.info("Starting a new election.")
      val m = Messages.Leader.Elector.StartElection(msg.leaders)
      context.actorSelection("/user/leader/observers/*") ! m
      self ! m
    }
    case msg: Messages.Leader.Elector.StartElection => throw new SpinalException("Inheriting Elector should respond to StartElection.")
    case msg: Messages.Leader.Elector.ElectionCompleted => {
      logger.info(s"Election completed amongst ${msg.allLeaders.size} leaders. New leader: ${msg.leader}")
      msg.leader match {
        case SelfLeader => {
          logger.info("I'm the leader! Woohoo!")
          logger.info("Starting discoverers and publishers...")

          context.actorSelection("../observers/*") ! Messages.Leader.DiscovererBoss.Initialize

          context.actorSelection("..") ! Messages.Leader.IsActiveLeader
        }
        case _ => {
          logger.info("Not the leader. Going dormant.")
        }
      }
    }
  }
}
