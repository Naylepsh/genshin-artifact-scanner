import Capture.ScreenCapture._
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
  //  val coords = RectangleCoordinates(new Point(20, 150), new Point(165, 180)) // stat name coords
  val coords = RectangleCoordinates(new Point(45, 350), new Point(420, 500))

  for (i <- 5 to 5) {
    println(s"---------------- $i ---------------")
    val pathToFile = s"F:/Code/artifact-helper/screen-capturer/artifact-$i.png"
    val image = ImageIO.read(new File(pathToFile))
    //    val subImage = getSubImage(image, coords)

    val mainStat = extractor.extractMainStat(image)
    print(mainStat)

    val level = extractor.extractLevel(image)
    println(level)

    val setName = extractor.extractSetName(image)
    println(setName)

    val subStats = extractor.extractSubStats(image)
    println(subStats)

    val rarity = extractor.extractRarity(image)
    println(rarity)

    //    val result = tesseract.doOCR(subImage)
    //    println(result)
  }

  println("Done")
}
