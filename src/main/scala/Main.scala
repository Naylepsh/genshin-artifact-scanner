import Capture.ScreenCapture._
import Extract.ArtifactImageExtractor.getSubImage
import Extract._

import java.awt.Point
import java.io.File
import javax.imageio.ImageIO

object Main extends App {
  //  val artifactCoordinates = RectangleCoordinates(new Point(1295, 120), new Point(1780, 670))
  //  val image = ScreenCapture.captureRectangle(artifactCoordinates)
  //  saveToFile(image, "./artifact")

  val dataPath = sys.env("TESSDATA")
  val language = "eng"
  val tesseract = TesseractWrapper(dataPath, language)
  val extractor = ArtifactImageExtractor(tesseract)

  //  val coords = RectangleCoordinates(new Point(20, 180), new Point(165, 220)) // stat value coords
  val coords = RectangleCoordinates(new Point(20, 150), new Point(165, 180)) // stat name coords

  val pathToFile = "F:/Code/artifact-helper/screen-capturer/artifact-0.png"
  val image = ImageIO.read(new File(pathToFile))
  val subImage = getSubImage(image, coords)

  val mainStat = extractor.extractMainStat(image)
  print(mainStat)

  val level = extractor.extractLevel(image)
  println(level)

  val setName = extractor.extractSetName(image)
  println(setName)

  //  val result = tesseract.doOCR(image)
  //  println(result)
  println("Done")
}
