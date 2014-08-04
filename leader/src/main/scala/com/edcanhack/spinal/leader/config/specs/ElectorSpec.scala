package com.edcanhack.spinal.leader.config.specs

import akka.actor.{ActorRef, ActorRefFactory, Props}
import com.edcanhack.spinal.leader.config.LeaderConfiguration
import com.edcanhack.spinal.leader.electors.SolitaryElector

trait ElectorSpec {
  def initializeElector(factory: ActorRefFactory, configuration: LeaderConfiguration): ActorRef
}
case class SolitaryElectorSpec() extends ElectorSpec {
  def initializeElector(factory: ActorRefFactory, configuration: LeaderConfiguration): ActorRef = {
    factory.actorOf(Props(classOf[SolitaryElector], configuration), "elector")
  }
}