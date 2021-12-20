package Extract

import Capture.ScreenCapture.RectangleCoordinates

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Try

case class ArtifactImageExtractor(tesseract: TesseractWrapper) {

  import Extract.ArtifactImageExtractor._

  def extractLevel(image: BufferedImage): Try[Int] = {
    val levelImage = getSubImage(image, levelCoordinates)
    extractRawData(levelImage).map(ArtifactStringExtractor.extractLevel).map(_.get)
  }

  private def extractRawData(image: BufferedImage): Try[String] =
    tesseract.doOCR(image)

  def extractSetName(image: BufferedImage): Try[String] = {
    val setNameImage = getSubImage(image, setNameCoordinates)
    extractRawData(setNameImage).map(ArtifactStringExtractor.extractName).map(_.get)
  }

  def extractMainStat(image: BufferedImage): Try[(String, Float)] = ???
}

object ArtifactImageExtractor {
  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))
  private val setNameCoordinates = RectangleCoordinates(new Point(20, 510), new Point(335, 540))

  def getSubImage(image: BufferedImage, coordinates: RectangleCoordinates): BufferedImage =
    image.getSubimage(coordinates.topLeft.x, coordinates.topLeft.y, coordinates.width, coordinates.height)
}
