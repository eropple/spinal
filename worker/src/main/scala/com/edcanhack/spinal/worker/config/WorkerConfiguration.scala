package com.edcanhack.spinal.worker.config

import com.edcanhack.spinal.commons.config.{ConfigurationParameters, Configuration}
import com.edcanhack.spinal.worker.config.specs.{EffectorSpec, SubscriberSpec, WorkerObserverSpec}

trait WorkerConfiguration extends Configuration {
  def parameters: WorkerParametersBase

  def subscriber: SubscriberSpec
  def workerObservers: Seq[WorkerObserverSpec]
  def effectors: Seq[EffectorSpec]
}

trait WorkerParametersBase extends ConfigurationParameters {

}
class WorkerParameters() extends WorkerParametersBase