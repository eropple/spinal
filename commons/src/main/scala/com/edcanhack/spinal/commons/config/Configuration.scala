package com.edcanhack.spinal.commons.config

import java.io.File

import com.edcanhack.spinal.commons.config.specs.LocatorSpec
import com.twitter.util.Eval
import com.typesafe.scalalogging.slf4j.StrictLogging

class ConfigResponse
case class ConfigOk[TConfigType <: Configuration](config: TConfigType) extends ConfigResponse
case class ConfigErrors(errors: Seq[String]) extends ConfigResponse

trait Configuration {
  def parameters: ConfigurationParameters

  def locator: LocatorSpec

  def sanityCheck(): ConfigResponse
}

abstract class ConfigurationParameters private[spinal]()

object Configuration extends StrictLogging {
  def load[TConfigType <: Configuration](): ConfigResponse = {
    Option(System.getProperty("spinal.config_file")) match {
      case None => ConfigErrors(Seq("Loading leader configuration: must be specified as an absolute path with -Dspinal.config_file"))
      case Some(path: String) => {
        logger.info(s"Attempting to load configs from '${path}'.")
        val configFile = new File(path)
        if (!configFile.exists() || !configFile.canRead) {
          ConfigErrors(Seq(s"Loading leader configuration: file '${path}' does not exist or is not readable."))
        } else {
          try {
            Eval[TConfigType](configFile).sanityCheck()
          } catch {
            case t: Throwable => {
              ConfigErrors(Seq(s"Error parsing '${path}': '${t.getClass.getSimpleName}': ${t.getMessage}"))
            }
          }
        }
      }
    }
  }
}

