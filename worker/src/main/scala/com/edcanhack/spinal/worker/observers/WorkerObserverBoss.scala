package com.edcanhack.spinal.worker.observers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.worker.config.WorkerConfiguration

// TODO: refactor the Boss classes into something easier to deal with
class WorkerObserverBoss(val config: WorkerConfiguration) extends BaseActor[WorkerConfiguration] {
  def receive = {
    case Messages.Worker.WorkerObserverBoss.Initialize => {
      logger.info(s"Creating ${config.workerObservers.size} observers...")
      config.workerObservers.view.zipWithIndex.foreach { case (o, i) => o.initializeWorkerObserver(context, config, i) }
    }
  }
}
