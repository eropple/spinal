package com.edcanhack.spinal.worker.config.specs

import akka.actor.{Props, ActorRefFactory}
import com.edcanhack.spinal.worker.config.WorkerConfiguration
import com.edcanhack.spinal.worker.effectors.EchoingEffector

trait EffectorSpec {
  def initializeEffector(factory: ActorRefFactory, configuration: WorkerConfiguration, actorIdx: Long)
}

case class EchoingEffectorSpec() extends EffectorSpec {
  def initializeEffector(factory: ActorRefFactory, configuration: WorkerConfiguration, actorIdx: Long) {
    factory.actorOf(Props(classOf[EchoingEffector], configuration), s"${actorIdx}-EchoingEffector")
  }
}