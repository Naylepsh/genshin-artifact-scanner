package Utils.Image

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.util.Try

object ImageUtils {
  def open(source: String): Try[BufferedImage] = Try {
    ImageIO.read(new File(source))
  }

  def save(image: BufferedImage, dest: String, format: String = "png"): Unit =
    ImageIO.write(image, format, new File(enforceFormatOnFilename(dest, format)))

  def enforceFormatOnFilename(filename: String, defaultFormat: String): String = {
    if (filename.endsWith(s".$defaultFormat"))
      filename
    else
      s"$filename.$defaultFormat"
  }
}
