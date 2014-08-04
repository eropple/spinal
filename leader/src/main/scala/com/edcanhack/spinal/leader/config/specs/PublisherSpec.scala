package com.edcanhack.spinal.leader.config.specs

import akka.actor.{Props, ActorRef, ActorRefFactory}
import com.edcanhack.spinal.leader.config.LeaderConfiguration
import com.edcanhack.spinal.leader.publishers.{NullPublisher, AkkaPublisher}

trait PublisherSpec {
  def initializePublisher(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef
}

case class NullPublisherSpec() extends PublisherSpec {
  def initializePublisher(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef = {
    factory.actorOf(Props(classOf[NullPublisher], configuration), s"${actorIdx}-NullPublisher")
  }
}

// That path assumes that the publisher ends up at /user/leader/publishers/N-PublisherName and goes relative from there.
case class AkkaPublisherSpec(val target: String = "../../../worker/subscriber") extends PublisherSpec {
  def initializePublisher(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef = {
    factory.actorOf(Props(classOf[AkkaPublisher], configuration, target), s"${actorIdx}-AkkaPublisher")
  }
}