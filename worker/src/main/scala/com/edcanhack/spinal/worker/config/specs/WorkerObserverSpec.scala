package com.edcanhack.spinal.worker.config.specs

import akka.actor.ActorRefFactory
import com.edcanhack.spinal.worker.config.WorkerConfiguration

trait WorkerObserverSpec {
  def initializeWorkerObserver(factory: ActorRefFactory, configuration: WorkerConfiguration, actorIdx: Long)
}
