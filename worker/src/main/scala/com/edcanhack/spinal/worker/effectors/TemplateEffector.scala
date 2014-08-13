package com.edcanhack.spinal.worker.effectors

import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{Files, Path, Paths}

import com.edcanhack.spinal.commons.state.Universe
import com.edcanhack.spinal.commons.util.Chmod
import com.edcanhack.spinal.worker.config.WorkerConfiguration
import com.edcanhack.spinal.worker.effectors.TemplateEffector.{Output, Templater}
import com.google.common.base.Charsets
import com.google.common.hash.{HashCode, Hashing}
import com.typesafe.scalalogging.slf4j.LazyLogging

class TemplateEffector(val config: WorkerConfiguration, val templater: Templater, val output: Output) extends Effector {
  override def perform(universe: Universe): Unit = {
    logger.info(s"Rendering universe to ${templater.getClass.getSimpleName}.")
    val rendered = templater.render(universe)

    logger.info(s"Writing template (${rendered.size} bytes) to ${output}.")
    output.write(rendered)
  }
}

object TemplateEffector {
  trait Templater {
    def render(universe: Universe): String
  }

  trait Output extends LazyLogging {
    def write(str: String)
  }
  object Output {
    case class Multi(outputs: Seq[Output]) extends Output {
      override def write(str: String): Unit = outputs.foreach(o => o.write(str))
    }

    case class Debounced(output: Output) extends Output {
      private var lastHash: HashCode = null
      private val sha1 = Hashing.sha256()

      override def write(str: String): Unit = {
        val currentHash = sha1.hashString(str, Charsets.UTF_8)
        if (currentHash.equals(lastHash)) {
          logger.debug("Attempted to write template with same hash; skipping.")
        } else {
          logger.debug("Template has new hash; passing to child.")
          output.write(str)
          lastHash = currentHash
        }
      }
    }

    case class File(path: Path, permissions: Set[PosixFilePermission]) extends Output {
      override def write(str: String): Unit = {
        logger.debug(s"Writing template to '${path.toString}'.")
        Files.deleteIfExists(path)
        Files.write(path, str.getBytes(Charsets.UTF_8))
        Chmod.chmod(path, permissions)
      }
    }
    object File {
      def apply(path: String, mode: String): File = File(Paths.get(path), Chmod.nioPermSet(mode))
    }

    case class Stderr() extends Output {
      override def write(str: String): Unit = {
        System.err.print(str)
      }
    }
  }
}
