package com.edcanhack.spinal.extensions.ec2.leader.discoverers

import com.edcanhack.spinal.leader.config.LeaderConfiguration
import com.edcanhack.spinal.leader.discoverers.Discoverer
import com.amazonaws.services.ec2.AmazonEC2Client
import scala.collection.JavaConversions._
import com.edcanhack.spinal.commons.state.Routable
import com.amazonaws.services.ec2.model.{Tag, Instance, DescribeInstancesResult}
import com.amazonaws.auth.{DefaultAWSCredentialsProviderChain, AWSCredentialsProvider}
import play.api.libs.json.Json


class EC2TagDiscoverer(val config: LeaderConfiguration, val priority: Int,
                       ec2Credentials: Option[AWSCredentialsProvider],
                       val routingTagName: String, val dieOnFail: Boolean) extends Discoverer {
  private val credentialProvider = ec2Credentials getOrElse new DefaultAWSCredentialsProviderChain()
  protected val EC2 = new AmazonEC2Client(credentialProvider)

  override def discover(): Unit = {
    try {
      logger.info(s"Attempting to discover state of EC2 via tag name '${routingTagName}'.")
      val result: DescribeInstancesResult = EC2.describeInstances()
      val instances = result.getReservations.flatMap(r => r.getInstances)

      instances map processInstances
    } catch {
      case ex: Exception => {
        logger.error(s"Exception received in EC2TagDiscoverer::discover.", ex)
        if (dieOnFail) {
          logger.error("dieOnFail is true, so rethrowing. Bombs away!")
          throw ex
        }
      }
    }
  }


  protected def processInstances(instance: Instance): Seq[Routable] = {
    try {
      instance.getTags.find(_.getKey == routingTagName) match {
        case None => Seq()
        case Some(tag: Tag) => processRoutingTag(instance, tag.getValue)
      }
    } catch {
      case ex: Exception => {
        logger.warn(s"Failed to parse tags for '${instance.getInstanceId}'.", ex)
        Seq()
      }
    }
  }

  protected def processRoutingTag(instance: Instance, content: String): Seq[Routable] = ???
}