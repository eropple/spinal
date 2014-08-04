package com.edcanhack.spinal.leader.publishers

import com.edcanhack.spinal.commons.Messages
import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.leader.config.LeaderConfiguration

trait Publisher extends BaseActor[LeaderConfiguration] {
  def receive = {
    case Messages.Leader.Publisher.Publish(u: Universe) => publish(u)
  }

  def publish(universe: Universe): Unit
}
