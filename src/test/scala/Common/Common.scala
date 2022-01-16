package Common

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.util.Try

object Common {
  val pathToExistingArtifact: String =
    getClass.getResource("/artifacts/4-star-2-stats-plume.png").getPath

  def openImage(source: String): Try[BufferedImage] = Try {
    ImageIO.read(new File(source))
  }
}
