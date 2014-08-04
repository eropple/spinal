package com.edcanhack.spinal.commons.state

import scala.collection.immutable.MapProxy

case class Universe(self: Map[String, Seq[Routable]]) extends MapProxy[String, Seq[Routable]] {
  def merge(other: Universe, mergeType: Universe.MergeType): Universe = {
    Universe.empty
  }
}

object Universe {
  val empty = Universe(Map())

  abstract class MergeType private[state]()
  case object OverwriteMerge extends MergeType
  case object NormalMerge extends MergeType
}