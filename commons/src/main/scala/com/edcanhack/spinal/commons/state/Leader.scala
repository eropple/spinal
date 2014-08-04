package com.edcanhack.spinal.commons.state

abstract class Leader
case object NoLeader extends Leader
case object SelfLeader extends Leader
case class RemoteLeader(address: String, port: Int) extends Leader