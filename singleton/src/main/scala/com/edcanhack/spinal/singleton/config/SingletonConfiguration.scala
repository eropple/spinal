package com.edcanhack.spinal.singleton.config

import com.edcanhack.spinal.commons.config.specs.{LocatorSpec, SelfLocatorSpec}
import com.edcanhack.spinal.commons.config.{ConfigurationParameters, ConfigErrors, ConfigOk, ConfigResponse}
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.commons.state.Universe.NormalMerge
import com.edcanhack.spinal.leader.config.specs.{AkkaPublisherSpec, ElectorSpec, LeaderObserverSpec, SolitaryElectorSpec}
import com.edcanhack.spinal.leader.config.{LeaderConfiguration, LeaderParametersBase}
import com.edcanhack.spinal.worker.config.specs.{AkkaSubscriberSpec, EffectorSpec, WorkerObserverSpec}
import com.edcanhack.spinal.worker.config.{WorkerConfiguration, WorkerParametersBase}

import scala.collection.mutable.ListBuffer

/**
 * The core configuration for spinal. Uses Twitter's Eval library to evaluate a Scala
 * file to create an instance of LeaderConfiguration.
 */
trait SingletonConfiguration extends LeaderConfiguration with WorkerConfiguration {
  def parameters: SingletonParametersBase

  final val locator: LocatorSpec = SelfLocatorSpec()
  final val elector: ElectorSpec = SolitaryElectorSpec()
  def leaderObservers: Seq[LeaderObserverSpec]
  def workerObservers: Seq[WorkerObserverSpec]
  final val publishers = Seq(AkkaPublisherSpec())
  final val subscriber = AkkaSubscriberSpec()
  def effectors: Seq[EffectorSpec]

  override def sanityCheck(): ConfigResponse = {
    val errors = ListBuffer[String]()

    if (discoverers == null || discoverers.isEmpty) errors.append("At least one discoverer must be specified.")
    if (leaderObservers == null) errors.append("leaderObservers should be defined, even if it is empty.")
    if (workerObservers == null) errors.append("workerObservers should be defined, even if it is empty.")
    if (effectors == null) errors.append("effectors should be defined, even if it is empty.")

    if (errors.isEmpty)
      ConfigOk(this)
    else
      ConfigErrors(errors.toSeq)
  }
}

trait SingletonParametersBase extends ConfigurationParameters with LeaderParametersBase with WorkerParametersBase
case class SingletonParameters(universeMergeType: Universe.MergeType = NormalMerge)
  extends SingletonParametersBase
