package com.edcanhack.spinal.singleton

import akka.actor.{Props, ActorSystem}
import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.config.{ConfigErrors, ConfigOk, Configuration}
import com.edcanhack.spinal.leader.LeaderBoss
import com.edcanhack.spinal.singleton.config.SingletonConfiguration
import com.edcanhack.spinal.worker.WorkerBoss
import com.typesafe.scalalogging.slf4j.StrictLogging

object Main extends StrictLogging {
  def main(argTokens: Array[String]) = {
    logger.info("Application starting.")

    val config: SingletonConfiguration = Configuration.load[SingletonConfiguration]() match {
      case err: ConfigErrors => {
        logger.error("Errors during configuration:")
        err.errors.foreach(e => logger.error(s" - ${e}"))
        System.exit(1)
        ??? // could throw an exception here but I think this is cleaner. will have to think about it.
      }
      case ConfigOk(config: SingletonConfiguration) => config
    }

    if (logger.underlying.isInfoEnabled) {
      logger.info(s"Locator: ${config.locator.toString}")
      logger.info(s"Elector: ${config.elector.toString}")
      logger.info(s"Discoverers: [${config.discoverers.mkString(", ")}]")
      logger.info(s"Leader Observers: [${config.leaderObservers.mkString(", ")}]")
      logger.info(s"Publishers: [${config.publishers.mkString(", ")}]")
      logger.info(s"Subscribers: [${config.subscriber.toString}]")
      logger.info(s"Worker Observers: [${config.workerObservers.mkString(", ")}]")
      logger.info(s"Effectors: [${config.effectors.mkString(", ")}]")
    }

    logger.info("Bootstrapping ActorSystem...")

    val system = ActorSystem("spinal")


    config.locator.initializeLocator(system, config)
    system.actorOf(Props(classOf[LeaderBoss], config), "leader") ! Messages.Leader.Initialize
    system.actorOf(Props(classOf[WorkerBoss], config), "worker") ! Messages.Worker.Initialize

    system.actorSelection("/user/locator") ! Messages.Locator.Initialize
    system.awaitTermination()
  }
}
