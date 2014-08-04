package com.edcanhack.spinal.worker.subscribers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.worker.config.WorkerConfiguration

case class AkkaSubscriber(val config: WorkerConfiguration) extends Subscriber {
  // `spinal` supports both pub/sub and polling subscribers; this is basically the former.
  def receive = {
    case Messages.Worker.Subscriber.PublishedTo(u: Universe) => {
      val msg = Messages.Worker.Effector.PerformEffect(u)
      context.actorSelection("../observers/*") ! msg
      context.actorSelection("../effectors/*") ! msg
    }
  }
}
