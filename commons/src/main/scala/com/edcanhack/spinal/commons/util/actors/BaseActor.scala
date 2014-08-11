package com.edcanhack.spinal.commons.util.actors

import akka.actor.Actor
import com.edcanhack.spinal.commons.config.Configuration
import com.typesafe.scalalogging.slf4j.LazyLogging

trait BaseActor[TConfigurationType <: Configuration] extends Actor with LazyLogging {
  def config: TConfigurationType

  override def preStart(): Unit = {
    super.preStart()
    logger.info(s"Created on path '${self.path}'.")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    super.preRestart(reason, message)
    logger.info(s"Restarting because of ${reason.getClass.getSimpleName}.")
  }
}
