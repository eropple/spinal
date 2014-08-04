package com.edcanhack.spinal.commons.config.specs

import akka.actor.{ActorRef, ActorRefFactory, Props}
import com.edcanhack.spinal.commons.config.Configuration
import com.edcanhack.spinal.commons.locators.SelfLocator

trait LocatorSpec {
  def initializeLocator(factory: ActorRefFactory, configuration: Configuration): ActorRef
}
case class SelfLocatorSpec() extends LocatorSpec {
  def initializeLocator(factory: ActorRefFactory, configuration: Configuration): ActorRef = {
    factory.actorOf(Props(classOf[SelfLocator], configuration), "locator")
  }
}