package com.edcanhack.spinal.worker.effectors

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.worker.config.WorkerConfiguration

// TODO: refactor the Boss classes into something easier to deal with
class EffectorBoss(val config: WorkerConfiguration) extends BaseActor[WorkerConfiguration] {
  def receive = {
    case Messages.Worker.EffectorBoss.Initialize => {
      logger.info(s"Creating ${config.effectors.size} effectors...")
      config.effectors.view.zipWithIndex.foreach { case (o, i) => o.initializeEffector(context, config, i) }
    }
  }
}
