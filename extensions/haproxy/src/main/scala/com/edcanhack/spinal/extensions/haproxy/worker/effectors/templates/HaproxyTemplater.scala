package com.edcanhack.spinal.extensions.haproxy.worker.effectors.templates

import com.edcanhack.spinal.worker.effectors.TemplateEffector.Templater
import com.edcanhack.spinal.commons.state.Universe

case class HaproxyTemplater() extends Templater {
  /**
   * Renders out an HAProxy template that represents the requested universe.
   *
   * Roughly in order of decomposition:
   * - universe is sorted into (host, port) -> Seq[HTTPRoutable]
   * - a `frontend` is created for every (host, port) tuple
   * - a frontend `acl` and a `use-backend` directive is created for every subsequent (host, path) tuple within it
   * - a `backend` is created for every (host, path) tuple; uses all healthchecks as `httpchk` options
   *   (HAProxy will not mark a node down unless ALL health checks in the set fail)
   * - each endpoint is a `server` within that backend
   *
   * @param universe
   * @return
   */
  override def render(universe: Universe): String = ???
}
