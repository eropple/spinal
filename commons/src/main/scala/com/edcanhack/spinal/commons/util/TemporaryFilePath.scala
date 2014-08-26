package com.edcanhack.spinal.commons.util

import java.nio.file.{Paths, Path, Files}

class TemporaryFilePath(dir: Path, prefix: String, suffix: String) {
  val path = Files.createTempFile(dir, prefix, suffix)

  def clean() = Files.deleteIfExists(path)
  override def finalize() = clean()
}

object TemporaryFilePath {
  def withTempFile[A](dir: Path = Paths.get("/tmp"), prefix: String = "tmp_", suffix: String = "")(block: => Path => A): A = {
    val tempFile = new TemporaryFilePath(dir, prefix, suffix)
    try {
      val retval = block(tempFile.path)
      tempFile.clean()

      retval
    } catch {
      case t: Throwable => throw t
    }
  }
}
