package com.edcanhack.spinal.leader.config

import com.edcanhack.spinal.commons.config._
import com.edcanhack.spinal.commons.config.specs.LocatorSpec
import com.edcanhack.spinal.leader.config.specs.{DiscovererSpec, ElectorSpec, LeaderObserverSpec, PublisherSpec}

import scala.collection.mutable.ListBuffer

/**
 * The core configuration for spinal. Uses Twitter's Eval library to evaluate a Scala
 * file to create an instance of LeaderConfiguration.
 */
trait LeaderConfiguration extends Configuration {
  def parameters: LeaderParametersBase

  def locator: LocatorSpec
  def elector: ElectorSpec

  def discoverers: Seq[DiscovererSpec]

  def leaderObservers: Seq[LeaderObserverSpec]
  def publishers: Seq[PublisherSpec]

  def sanityCheck(): ConfigResponse = {
    val errors = ListBuffer[String]()

    if (locator == null) errors.append("The locator must be defined.")
    if (elector == null) errors.append("The elector must be defined.")
    if (discoverers == null || discoverers.isEmpty) errors.append("At least one discoverer must be specified.")
    if (leaderObservers == null) errors.append("The observer Seq should be set, even if it is empty.")
    if (publishers == null || publishers.isEmpty) errors.append("At least one publisher must be specified.")

    if (errors.isEmpty)
      ConfigOk(this)
    else
      ConfigErrors(errors.toSeq)
  }
}

trait LeaderParametersBase extends ConfigurationParameters {
}
case class LeaderParameters()
  extends LeaderParametersBase
