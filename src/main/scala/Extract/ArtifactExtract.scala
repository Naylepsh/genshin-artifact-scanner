package Extract

import Capture.ScreenCapture.RectangleCoordinates

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Try

case class ArtifactExtract(tesseract: TesseractWrapper) {

  import Extract.ArtifactExtract._

  def extractLevel(image: BufferedImage): Try[Int] = Try {
    val levelImage = image.getSubimage(
      levelCoordinates.topLeft.x, levelCoordinates.topLeft.y, levelCoordinates.width, levelCoordinates.height)
    val rawData = extractRawData(levelImage).get
    ArtifactExtract.extractLevel(rawData).get
  }

  private def extractRawData(image: BufferedImage): Try[String] =
    tesseract.doOCR(image)
}

object ArtifactExtract {
  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))

  def extractLevel(string: String): Try[Int] = Try {
    val level = "[0-9]+".r.findFirstIn(string).get
    level.toInt
  }
}
