package com.edcanhack.spinal.worker.config.specs

import akka.actor.{ActorRef, Props, ActorRefFactory}
import com.edcanhack.spinal.worker.config.WorkerConfiguration
import com.edcanhack.spinal.worker.subscribers.AkkaSubscriber

trait SubscriberSpec {
  def initializeSubscriber(factory: ActorRefFactory, configuration: WorkerConfiguration): ActorRef
}

case class AkkaSubscriberSpec() extends SubscriberSpec {
  def initializeSubscriber(factory: ActorRefFactory, configuration: WorkerConfiguration): ActorRef = {
    factory.actorOf(Props(classOf[AkkaSubscriber], configuration), "subscriber")
  }
}