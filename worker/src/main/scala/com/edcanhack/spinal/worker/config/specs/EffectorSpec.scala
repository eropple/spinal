package com.edcanhack.spinal.worker.config.specs

import akka.actor.{Props, ActorRefFactory}
import com.edcanhack.spinal.worker.config.WorkerConfiguration
import com.edcanhack.spinal.worker.effectors.{TemplateEffector, EchoingEffector}
import com.edcanhack.spinal.worker.effectors.TemplateEffector.{Templater, Output}

trait EffectorSpec {
  def initializeEffector(factory: ActorRefFactory, configuration: WorkerConfiguration, actorIdx: Long)
}

case class EchoingEffectorSpec() extends EffectorSpec {
  def initializeEffector(factory: ActorRefFactory, configuration: WorkerConfiguration, actorIdx: Long) {
    factory.actorOf(Props(classOf[EchoingEffector], configuration), s"${actorIdx}-EchoingEffector")
  }
}

case class TemplateEffectorSpec(templater: Templater, output: Output) extends EffectorSpec {
  override def initializeEffector(factory: ActorRefFactory, configuration: WorkerConfiguration, actorIdx: Long): Unit = {
    factory.actorOf(Props(classOf[TemplateEffector], configuration, templater, output), s"${actorIdx}-TemplateEffector")
  }
}