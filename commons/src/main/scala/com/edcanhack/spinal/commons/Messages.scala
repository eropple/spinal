package com.edcanhack.spinal.commons

import com.edcanhack.spinal.commons.state.{Routable, Universe, Leader}

object Messages {
  object Locator {
    case object Initialize
    case object Locate
  }

  object Leader {
    case object Initialize
    case object GoInactive
    case object IsActiveLeader

    object Boss {
      case object Initialize
    }

    object Elector {
      case class NewLeaders(leaders: Set[Leader])
      case class StartElection(leaders: Set[Leader])
      case class ElectionCompleted(leader: Leader, allLeaders: Set[Leader])
    }

    object DiscovererBoss {
      case object Initialize
      case class DiscoveryReport(weight: Int, universe: Universe)
    }

    object Discoverer {
      case object StartDiscovery
      case class Report(routes: Seq[_ <: Routable])
    }

    object Publisher {
      case class Publish(universe: Universe)
    }
  }

  object Worker {
    case object Initialize

    object WorkerObserverBoss {
      case object Initialize
    }

    object EffectorBoss {
      case object Initialize
    }
    object Effector {
      case class PerformEffect(universe: Universe)
    }

    object Subscriber {
      case object Initialize
      case class PublishedTo(universe: Universe)
    }
  }
}
