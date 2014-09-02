package com.edcanhack.spinal.extensions.ec2.worker.effectors.outputs

import com.amazonaws.auth.{DefaultAWSCredentialsProviderChain, AWSCredentialsProvider}
import com.edcanhack.spinal.worker.effectors.TemplateEffector.Output

case class S3(bucket: String, key: String,
              private val s3Credentials: Option[AWSCredentialsProvider] = None) extends Output {

  private val actualCreds = s3Credentials.getOrElse(new DefaultAWSCredentialsProviderChain())

  override def write(str: String): Unit = {
    ???
  }
}
