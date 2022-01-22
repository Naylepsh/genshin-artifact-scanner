package Utils.Logging

import Utils.Image.ImageUtils.{enforceFormatOnFilename, save}

import java.awt.image.BufferedImage
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

object ImageLogger {
  def log(workDir: String)(image: BufferedImage, message: String): Unit = {
    val filename = createFilename(workDir)
    save(image, filename)
    saveToFile(message, filename)
  }

  private def saveToFile(message: String, dest: String): Unit = {
    val format = "txt"
    Files.write(Paths.get(enforceFormatOnFilename(dest, format)), message.getBytes(StandardCharsets.UTF_8))
  }

  private def createFilename(path: String): String = {
    val id = java.util.UUID.randomUUID().toString
    s"$path/$id"
  }
}
