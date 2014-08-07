package com.edcanhack.spinal.worker.effectors

import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.worker.config.WorkerConfiguration

class EchoingEffector(val config: WorkerConfiguration) extends Effector {
  def perform(universe: Universe): Unit = {
    for (r <- universe.httpRoutes) {
      logger.info(s"${r.host}${r.path} @ ${r.sourcePort}: ${r.endpoints}")
    }
  }
}
