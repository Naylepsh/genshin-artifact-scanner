package Extract

import Capture.ScreenCapture.RectangleCoordinates

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Try

case class ArtifactExtractor(tesseract: TesseractWrapper) {

  import Extract.ArtifactExtractor._

  def extractLevel(image: BufferedImage): Try[Int] = {
    val levelImage = getSubImage(image, levelCoordinates)
    extractRawData(levelImage).map(ArtifactExtractor.extractLevel).map(_.get)
  }

  private def extractRawData(image: BufferedImage): Try[String] =
    tesseract.doOCR(image)

  def extractSetName(image: BufferedImage): Try[String] = {
    val setNameImage = getSubImage(image, setNameCoordinates)
    extractRawData(setNameImage).map(ArtifactExtractor.extractSetName).map(_.get)
  }
}

object ArtifactExtractor {
  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))
  private val setNameCoordinates = RectangleCoordinates(new Point(20, 510), new Point(335, 540))

  def extractLevel(string: String): Option[Int] =
    "[0-9]+".r.findFirstIn(string).map(_.toInt)

  def extractSetName(rawData: String): Option[String] =
    "[a-zA-Z ]+".r.findFirstIn(rawData)

  def getSubImage(image: BufferedImage, coordinates: RectangleCoordinates): BufferedImage =
    image.getSubimage(coordinates.topLeft.x, coordinates.topLeft.y, coordinates.width, coordinates.height)
}
