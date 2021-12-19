package Extract

import Capture.ScreenCapture.RectangleCoordinates

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Try

object ArtifactExtract {
  private val dataPath = sys.env("TESSDATA")
  private val language = "eng"
  private val tesseract = TesseractWrapper(dataPath, language)

  private val levelCoordinates = RectangleCoordinates(new Point(30, 310), new Point(80, 340))

  def extractLevel(image: BufferedImage): Try[Int] = Try {
    val levelImage = image.getSubimage(levelCoordinates.topLeft.x, levelCoordinates.topLeft.y, levelCoordinates.width, levelCoordinates.height)
    val rawData = extractRawData(levelImage).get
    extractLevel(rawData).get
  }

  private def extractRawData(image: BufferedImage): Try[String] =
    tesseract.doOCR(image)

  private def extractLevel(string: String): Try[Int] = Try {
    val level = "[0-9]+".r.findFirstIn(string).get
    level.toInt
  }
}
