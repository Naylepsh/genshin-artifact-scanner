package Extraction

import Capture.ScreenCapture.RectangleCoordinates

import java.awt.image.BufferedImage
import java.awt.{Color, Point}
import scala.util.Try

case class ArtifactImageExtractor(tesseract: TesseractWrapper) {

  import Extraction.ArtifactImageExtractor._

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

  def extractMainStat(image: BufferedImage): Try[(String, Float)] = {
    val nameImage = getSubImage(image, mainStatNameCoordinates)
    val valueImage = getSubImage(image, mainStatValueCoordinates)

    for {
      name <- extractRawData(nameImage).map(ArtifactStringExtractor.extractName).map(_.get)
      value <- extractRawData(valueImage).map(ArtifactStringExtractor.extractFirstStatValue).map(_.get)
    } yield (name, value)
  }

  def extractSubStats(image: BufferedImage): Try[Map[String, Float]] = {
    val subStatsImage = getSubStatsSubImage(image)
    extractRawData(subStatsImage)
      .map(ArtifactStringExtractor.extractSubStats)
      .map(subStatsListToMap)
  }

  private def getSubStatsSubImage(image: BufferedImage): BufferedImage = {
    val coordinates = subStatsCoordinates(extractSubStatsNumber(image))
    getSubImage(image, coordinates)
  }

  def extractSubStatsNumber(image: BufferedImage): Int = {
    val setNameColor = new Color(92, 178, 86)
    val containsColor = lineContainsColor(image)(setNameColor) _
    val startX = 40
    val endX = 50
    val startY = 450
    val endY = 530
    val yDelta = 40
    val subStatLineOffset = 2

    //    Artifacts are guaranteed at least one sub stat.
    //    Start looking for set name with the assumption,
    //    that is has at least 2 sub stats
    startY.to(endY).by(yDelta).zipWithIndex
      .find(yAndIndex => containsColor(startX, endX, yAndIndex._1))
      .map(_._2 + subStatLineOffset)
      .getOrElse(1)
  }

  def lineContainsColor(image: BufferedImage)(color: Color)(startX: Int, endX: Int, y: Int): Boolean =
    startX to endX exists { x => new Color(image.getRGB(x, y)) == color }

  def extractRarity(image: BufferedImage): Int =
    rgbToRarity.getOrElse(new Color(image.getRGB(rarityPoint.x, rarityPoint.y)), 1)
}

object ArtifactImageExtractor {
  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))
  private val setNameCoordinates = RectangleCoordinates(new Point(20, 510), new Point(335, 540))
  private val mainStatValueCoordinates = RectangleCoordinates(new Point(20, 180), new Point(165, 220))
  private val mainStatNameCoordinates = RectangleCoordinates(new Point(20, 150), new Point(165, 180))
  private val rarityPoint = new Point(10, 10)
  private val subStatsCoordinates = Map(
    1 -> RectangleCoordinates(new Point(45, 350), new Point(420, 395)),
    2 -> RectangleCoordinates(new Point(45, 350), new Point(420, 430)),
    3 -> RectangleCoordinates(new Point(45, 350), new Point(420, 465)),
    4 -> RectangleCoordinates(new Point(45, 350), new Point(420, 500)),
  )

  private val rgbToRarity = Map[Color, Int](
    new Color(188, 105, 50) -> 5,
    new Color(161, 86, 224) -> 4,
    new Color(81, 128, 203) -> 3,
    new Color(42, 143, 114) -> 2
  )

  def getSubImage(image: BufferedImage, coordinates: RectangleCoordinates): BufferedImage =
    image.getSubimage(coordinates.topLeft.x, coordinates.topLeft.y, coordinates.width, coordinates.height)

  def subStatsListToMap(subStats: List[(String, Float)]): Map[String, Float] =
    subStats.foldLeft(Map[String, Float]()) { (map, subStat) => map + (subStat._1 -> subStat._2) }
}
