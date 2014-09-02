package com.edcanhack.spinal.extensions.ec2.leader.config.specs

import java.util.concurrent.TimeUnit

import com.edcanhack.spinal.leader.config.specs.DiscovererSpec
import akka.actor.{Props, ActorRef, ActorRefFactory}
import com.edcanhack.spinal.leader.config.LeaderConfiguration
import com.edcanhack.spinal.extensions.ec2.leader.discoverers.EC2TagDiscoverer
import com.amazonaws.auth.AWSCredentialsProvider

import scala.concurrent.duration.{Duration, FiniteDuration}

case class EC2TagDiscovererSpec(priority: Int,
                                pollingFrequency: FiniteDuration = Duration(5, TimeUnit.SECONDS),
                                ec2Credentials: Option[AWSCredentialsProvider] = None,
                                routingTagName: String = "spinal:routing",
                                dieOnFail: Boolean = false) extends DiscovererSpec {
  override def initializeDiscoverer(factory: ActorRefFactory, configuration: LeaderConfiguration, actorIdx: Long): ActorRef = {
    factory.actorOf(Props(classOf[EC2TagDiscoverer],
      configuration, priority, pollingFrequency, ec2Credentials, routingTagName, dieOnFail),
      s"${actorIdx}-EC2TagDiscoverer")
  }
}
