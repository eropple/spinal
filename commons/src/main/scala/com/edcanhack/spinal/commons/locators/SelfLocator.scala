package com.edcanhack.spinal.commons.locators

import com.edcanhack.spinal.commons.config.Configuration
import com.edcanhack.spinal.commons.state.{Leader, SelfLeader}

case class SelfLocator(config: Configuration) extends Locator() {
  override protected def locate(): Set[Leader] = {
    Set(SelfLeader)
  }
}
