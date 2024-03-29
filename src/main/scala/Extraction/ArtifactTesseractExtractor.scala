package Extraction

import Capture.ScreenCapture.RectangleCoordinates
import Entities.Artifact
import Entities.Artifact.SetName.SetName
import Entities.Artifact.StatName
import Entities.Artifact.StatName.StatName
import Utils.Image.ImageProcessor.{invert, monochrome}

import java.awt.image.BufferedImage
import java.awt.{Color, Point}
import scala.util.{Failure, Success, Try}


case class ArtifactTesseractExtractor(tesseract: TesseractWrapper)
  extends ArtifactFromImageExtractable
    with NumberExtractable {

  import Extraction.ArtifactTesseractExtractor._

  def extractArtifact(image: BufferedImage): Try[Artifact] = {
    val rarity = extractRarity(image)
    for {
      level <- extractLevel(image)
      slot <- extractSlot(image)
      mainStat <- extractMainStat(image)
      subStats <- extractSubStats(image)
      setName <- extractSetName(image)
      artifact <- Artifact(setName, slot, level, rarity, mainStat, subStats)
    } yield artifact
  }

  def extractLevel(image: BufferedImage): Try[Int] = {
    def transform(rawData: String): String =
      ArtifactTesseractCorrector.correctLevel(ArtifactTesseractCorrector.correctNumericValue(rawData))

    extractInt(getSubImage(image, levelCoordinates), transform)
  }

  def extractInt(image: BufferedImage): Try[Int] =
    extractInt(image, ArtifactTesseractCorrector.correctNumericValue)

  def extractInt(image: BufferedImage, transform: String => String): Try[Int] = {
    val result = extractRawData(image)
      .map(transform)
      .map(ArtifactStringExtractor.extractInt)
    tryOptToTry(new RuntimeException("Could not detect any numeric value"))(result)
  }

  def tryOptToTry[T](onNone: Throwable)(tryOption: Try[Option[T]]): Try[T] = tryOption match {
    case Success(Some(result)) => Success(result)
    case Success(None) => Failure(onNone)
    case Failure(exception) => Failure(exception)
  }

  private def extractRawData(image: BufferedImage): Try[String] =
    tesseract.doOCR(image)

  def extractMainStat(image: BufferedImage): Try[StatName] = {
    def clean(image: BufferedImage): BufferedImage =
      monochrome(invert(image))

    def attachPercentageIfNeeded(rawData: String)(statName: String): String =
      if (rawData.contains('%')) s"$statName%" else statName

    val nameImage = clean(getSubImage(image, mainStatNameCoordinates))
    val valueImage = clean(getSubImage(image, mainStatValueCoordinates))

    val result = extractRawData(valueImage).flatMap(rawData => {
      extractRawData(nameImage)
        .map(ArtifactTesseractCorrector.correctStatName)
        .map(ArtifactStringExtractor.extractName)
        .map(_.map(attachPercentageIfNeeded(rawData)))
        .map(_.flatMap(StatName.fromString))
    })

    tryOptToTry(new RuntimeException("Could not detect the main stat"))(result)
  }

  def extractSlot(image: BufferedImage): Try[String] = {
    def firstWord(text: String): String = text.split(' ').head

    val slotSubImage = getSubImage(image, slotCoordinates)
    val result = extractRawData(slotSubImage)
      .map(ArtifactStringExtractor.extractName)
      .map(_.map(firstWord))
    tryOptToTry(new RuntimeException("Could not detect slot"))(result)
  }

  def extractSetName(image: BufferedImage): Try[SetName] = {
    val setNameImage = getSetNameSubImage(image)
    val rawData = extractRawData(setNameImage)
    val result = rawData
      .map(ArtifactTesseractCorrector.correctSetName)
      .map(ArtifactStringExtractor.extractSetName)
    tryOptToTry(new RuntimeException(s"Could not detect set name on $rawData"))(result)
  }

  def extractSubStatsNumber(image: BufferedImage): Int = {
    val setNameColor = new Color(92, 178, 86)
    val containsColor = lineContainsColor(image)(setNameColor) _
    val startX = 40
    val endX = 60
    val startY = 450
    val endY = 530
    val yDelta = 40
    val subStatLineOffset = 2

    //    Artifacts are guaranteed at least one sub stat.
    //    Start looking for set name with the assumption,
    //    that it has at least 2 sub stats
    startY.to(endY).by(yDelta).zipWithIndex
      .find { case (y, _) => containsColor(startX, endX, y) }
      .map { case (_, index) => index + subStatLineOffset }
      .getOrElse(1)
  }

  def lineContainsColor(image: BufferedImage)(color: Color)(startX: Int, endX: Int, y: Int): Boolean =
    startX to endX exists { x => new Color(image.getRGB(x, y)) == color }

  def extractSubStats(image: BufferedImage): Try[Map[StatName, Double]] = {
    val subStatsImage = monochrome(getSubStatsSubImage(image), 400)
    extractRawData(subStatsImage)
      .map(ArtifactTesseractCorrector.correctSubStats)
      .map(ArtifactStringExtractor.extractSubStats)
      .map(subStatsListToMap)
  }

  def extractRarity(image: BufferedImage): Int =
    rgbToRarity(new Color(image.getRGB(rarityPoint.x, rarityPoint.y)))


  private def getSetNameSubImage(image: BufferedImage): BufferedImage =
    getSubStatsDependantSubImage(subStatsNumberToSetNameCoordinates)(image)

  private def getSubStatsDependantSubImage(subStatsNumberToCoordinates: Map[Int, RectangleCoordinates])
                                          (image: BufferedImage): BufferedImage = {
    val coordinates = subStatsNumberToCoordinates(extractSubStatsNumber(image))
    getSubImage(image, coordinates)
  }

  private def getSubStatsSubImage(image: BufferedImage): BufferedImage =
    getSubStatsDependantSubImage(subStatsNumberToSubStatsCoordinates)(image)
}

object ArtifactTesseractExtractor {
  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))
  private val subStatsNumberToSetNameCoordinates = Map(
    1 -> RectangleCoordinates(new Point(20, 405), new Point(360, 435)),
    2 -> RectangleCoordinates(new Point(20, 440), new Point(360, 470)),
    3 -> RectangleCoordinates(new Point(20, 475), new Point(360, 505)),
    4 -> RectangleCoordinates(new Point(20, 510), new Point(360, 540))
  )
  private val slotCoordinates = RectangleCoordinates(new Point(20, 65), new Point(300, 95))
  private val mainStatValueCoordinates = RectangleCoordinates(new Point(20, 180), new Point(165, 220))
  private val mainStatNameCoordinates = RectangleCoordinates(new Point(20, 150), new Point(250, 180))
  private val rarityPoint = new Point(10, 10)
  private val subStatsNumberToSubStatsCoordinates = Map(
    1 -> RectangleCoordinates(new Point(45, 350), new Point(420, 400)),
    2 -> RectangleCoordinates(new Point(45, 350), new Point(420, 430)),
    3 -> RectangleCoordinates(new Point(45, 350), new Point(420, 470)),
    4 -> RectangleCoordinates(new Point(45, 350), new Point(420, 510)),
  )

  def getSubImage(image: BufferedImage, coordinates: RectangleCoordinates): BufferedImage =
    image.getSubimage(coordinates.topLeft.x, coordinates.topLeft.y, coordinates.width, coordinates.height)

  def subStatsListToMap(subStats: List[(StatName, Double)]): Map[StatName, Double] =
    subStats.foldLeft(Map[StatName, Double]())(_ + _)

  private def rgbToRarity(color: Color): Int = {
    def isLow(value: Int): Boolean = value < 60

    def isMedium(value: Int): Boolean = 60 <= value && value < 140

    def isHigh(value: Int): Boolean = 140 <= value

    val r = color.getRed
    val g = color.getGreen
    val b = color.getBlue

    val isOrangeLike = isHigh(r) && isMedium(g) && isLow(b)
    val isPurpleLike = isHigh(r) && !isHigh(g) && isHigh(b)
    val isBlueLike = !isHigh(r) && !isHigh(g) && isHigh(b)
    val isGreenLike = isLow(r) && isHigh(g) && !isHigh(b)

    if (isOrangeLike)
      5
    else if (isPurpleLike)
      4
    else if (isBlueLike)
      3
    else if (isGreenLike)
      2
    else
      1
  }
}
