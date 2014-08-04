package com.edcanhack.spinal.worker.subscribers

import com.edcanhack.spinal.commons.util.actors.BaseActor
import com.edcanhack.spinal.worker.config.WorkerConfiguration

/**
 * A listener is the "trigger" for a Spinal worker. It receives updates from some external
 * source--from the HTTP endpoint it exposes, from a subscribed change within Zookeeper,
 * or whatever other method--and
 */
trait Subscriber extends BaseActor[WorkerConfiguration] {

}
