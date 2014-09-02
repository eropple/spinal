package com.edcanhack.spinal.worker.effectors

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.worker.config.WorkerConfiguration

trait Effector extends BaseActor[WorkerConfiguration]{
  def receive = {
    case Messages.Worker.Effector.PerformEffect(u: Universe) => {
      logger.info(s"Universe of ${u.httpRoutes.size} HTTP routes received; performing effect.")
      perform(u)
    }
  }

  def perform(universe: Universe): Unit
}
