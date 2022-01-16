package Utils.Logging

import java.awt.image.BufferedImage
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO

object ImageLogger {
  def log(filenameStrategy: => String)(image: BufferedImage, message: String): Unit = {
    val filename = filenameStrategy
    saveToFile(image, filename)
    saveToFile(message, filename)
  }

  private def saveToFile(image: BufferedImage, dest: String): Unit = {
    val format = "png"
    ImageIO.write(image, format, new File(enforceFormatOnFilename(dest, format)))
  }

  private def enforceFormatOnFilename(filename: String, defaultFormat: String): String = {
    if (filename.endsWith(s".$defaultFormat"))
      filename
    else
      s"$filename.$defaultFormat"
  }

  private def saveToFile(message: String, dest: String): Unit = {
    val format = "txt"
    Files.write(Paths.get(enforceFormatOnFilename(dest, format)), message.getBytes(StandardCharsets.UTF_8))
  }

}
