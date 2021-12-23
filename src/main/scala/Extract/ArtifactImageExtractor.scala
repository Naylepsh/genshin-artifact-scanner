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

  def extractSetName(image: BufferedImage): Try[String] = {
    val setNameImage = getSubImage(image, setNameCoordinates)
    extractRawData(setNameImage).map(ArtifactStringExtractor.extractName).map(_.get)
  }

  private def extractRawData(image: BufferedImage): Try[String] =
    tesseract.doOCR(image)

  def extractMainStat(image: BufferedImage): Try[(String, Float)] = {
    val nameImage = getSubImage(image, mainStatNameCoordinates)
    val valueImage = getSubImage(image, mainStatValueCoordinates)

    for {
      name <- extractRawData(nameImage).map(ArtifactStringExtractor.extractName).map(_.get)
      value <- extractRawData(valueImage).map(ArtifactStringExtractor.extractFirstStatValue).map(_.get)
    } yield (name, value)
  }

  def extractSubStats(image: BufferedImage): Try[Map[String, Float]] = {
    val subStatsImage = getSubImage(image, subStatsCoordinates)
    extractRawData(subStatsImage)
      .map(ArtifactStringExtractor.extractSubStats)
      .map(subStatsListToMap)
  }
}

object ArtifactImageExtractor {
  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))
  private val setNameCoordinates = RectangleCoordinates(new Point(20, 510), new Point(335, 540))
  private val mainStatValueCoordinates = RectangleCoordinates(new Point(20, 180), new Point(165, 220))
  private val mainStatNameCoordinates = RectangleCoordinates(new Point(20, 150), new Point(165, 180))
  private val subStatsCoordinates = RectangleCoordinates(new Point(45, 350), new Point(420, 500))


  def getSubImage(image: BufferedImage, coordinates: RectangleCoordinates): BufferedImage =
    image.getSubimage(coordinates.topLeft.x, coordinates.topLeft.y, coordinates.width, coordinates.height)

  def subStatsListToMap(subStats: List[(String, Float)]): Map[String, Float] =
    subStats.foldLeft(Map[String, Float]()) { (map, subStat) => map + (subStat._1 -> subStat._2) }
}
