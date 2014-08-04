package com.edcanhack.spinal.worker.effectors

import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.worker.config.WorkerConfiguration

class EchoingEffector(val config: WorkerConfiguration) extends Effector {
  def perform(universe: Universe): Unit = {
    for (pair <- universe) {
      logger.info(s"${pair._1} -> ${pair._2}")
    }
  }
}
