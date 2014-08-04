package com.edcanhack.spinal.worker

import akka.actor.Props
import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.Messages.Worker.Initialize
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.worker.config.WorkerConfiguration
import com.edcanhack.spinal.worker.effectors.EffectorBoss
import com.edcanhack.spinal.worker.observers.WorkerObserverBoss

class WorkerBoss(val config: WorkerConfiguration) extends BaseActor[WorkerConfiguration] {
  def receive = {
    case Initialize => {
      logger.info("Initializing worker actors.")

      config.subscriber.initializeSubscriber(context, config) ! Messages.Worker.Subscriber.Initialize
      context.actorOf(Props(classOf[WorkerObserverBoss], config), "observers") ! Messages.Worker.WorkerObserverBoss.Initialize
      context.actorOf(Props(classOf[EffectorBoss], config), "effectors") ! Messages.Worker.EffectorBoss.Initialize
    }
  }
}
