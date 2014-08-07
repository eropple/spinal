import com.edcanhack.spinal.singleton.config._
import com.edcanhack.spinal.leader.config._
import com.edcanhack.spinal.leader.config.specs._
import com.edcanhack.spinal.commons.config.specs._
import com.edcanhack.spinal.worker.config.specs._
import com.edcanhack.spinal.commons.state._

new SingletonConfiguration {
  val parameters = SingletonParameters()

  val discoverers: Seq[DiscovererSpec] = 
    Seq(StaticUniverseDiscovererSpec(
          100,
          Universe(Map(

          ))
        )
    )
  val leaderObservers: Seq[LeaderObserverSpec] = Seq()
  val workerObservers: Seq[WorkerObserverSpec] = Seq()
  val effectors: Seq[EffectorSpec] = Seq(EchoingEffectorSpec())
}