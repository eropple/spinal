package com.edcanhack.spinal.commons.util

import com.typesafe.scalalogging.slf4j.StrictLogging
import com.edcanhack.spinal.commons.SpinalException
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{Files, Paths, Path}
import scala.collection.JavaConversions._

object Chmod extends StrictLogging {
  private val READ = 4
  private val WRITE = 2
  private val EXECUTE = 1


  def chmod(path: String, mode: String): Unit = {
    chmod(Paths.get(path), nioPermSet(mode))
  }

  def chmod(path: Path, perms: Set[PosixFilePermission]): Unit = {
    Files.setPosixFilePermissions(path, perms)
  }

  def nioPermSet(mode: String): Set[PosixFilePermission] = {
    import PosixFilePermission._

    if (mode.size < 3 || mode.size > 4)
      throw new SpinalException(s"nioPermSet() expects a mode of 3 or 4 characters, got '${mode}' (${mode.size}).")

    if (mode.size == 4 && mode.charAt(0) != '0')
      throw new SpinalException(s"nioPermSet() can't handle special bits; leading bit char of '${mode}' must be 0.")

    val m = if (mode.size == 3) mode else mode.substring(1)

    val owner = m(0).toInt
    val group = m(1).toInt
    val other = m(2).toInt

    val output = scala.collection.mutable.Set[PosixFilePermission]()

    if ((owner & READ) == READ) output.add(OWNER_READ)
    if ((owner & WRITE) == WRITE) output.add(OWNER_WRITE)
    if ((owner & EXECUTE) == EXECUTE) output.add(OWNER_EXECUTE)

    if ((group & READ) == READ) output.add(GROUP_READ)
    if ((group & WRITE) == WRITE) output.add(GROUP_WRITE)
    if ((group & EXECUTE) == EXECUTE) output.add(GROUP_EXECUTE)

    if ((other & READ) == READ) output.add(OTHERS_READ)
    if ((other & WRITE) == WRITE) output.add(OTHERS_WRITE)
    if ((other & EXECUTE) == EXECUTE) output.add(OTHERS_EXECUTE)

    output.toSet
  }
}
