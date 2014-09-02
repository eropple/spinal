package com.edcanhack.spinal.extensions.haproxy

import com.edcanhack.spinal.commons.state.HTTPRoutable

/**
 * Created by ed on 8/8/14.
 */
object Formatting {
  def pad(str: String, length: Int) = str.padTo(length, " ").mkString("")

  def routeName(r: HTTPRoutable): String = r.path match {
    case None => s"${r.host}:${r.sourcePort}"
    case Some(p: String) => s"${r.host}:${r.sourcePort}::${p.replace("/", "-")}"
  }
}
