package com.edcanhack.spinal.worker.effectors

import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.worker.config.WorkerConfiguration
import java.io.{ByteArrayInputStream, InputStream}
import java.nio.file.{Files, Paths, Path}
import java.nio.file.attribute.PosixFilePermission
import com.edcanhack.spinal.commons.util.Chmod
import com.edcanhack.spinal.worker.effectors.TemplateEffector.{Templater, Output}
import com.twitter.io.Charsets
import resource._
import com.google.common.io.ByteStreams

class TemplateEffector(val config: WorkerConfiguration, templater: Templater, output: Output) extends Effector {
  override def perform(universe: Universe): Unit = {
    logger.info(s"Rendering universe to ${templater.getClass.getSimpleName}.")
    val rendered = templater.render(universe).getBytes(Charsets.Utf8)
    logger.info(s"Writing template (${rendered.size} bytes) to ${output}.")
    for (stream <- managed(new ByteArrayInputStream(rendered))) {
      output.write(stream)
    }
  }
}

object TemplateEffector {
  trait Templater {
    def render(universe: Universe): String
  }

  trait Output {
    def write(stream: InputStream)
  }
  object Output {
    case class Multi(outputs: Seq[Output]) extends Output {
      override def write(stream: InputStream): Unit = outputs.foreach(o => o.write(stream))
    }

    case class File(path: Path, permissions: Set[PosixFilePermission]) extends Output {
      override def write(stream: InputStream): Unit = {
        Files.copy(stream, path)
        Chmod.chmod(path, permissions)
      }
    }
    object File {
      def apply(path: String, mode: String): File = File(Paths.get(path), Chmod.nioPermSet(mode))
    }

    case class Stderr() extends Output {
      override def write(stream: InputStream): Unit = {
        ByteStreams.copy(stream, System.err)
      }
    }
  }
}
