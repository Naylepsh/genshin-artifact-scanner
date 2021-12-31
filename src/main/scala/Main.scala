import Extraction._
import Scan.ArtifactScanner

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

  val outputDir = sys.env("OUTPUT_DIR")
  val scanner = ArtifactScanner(outputDir)
  val itemsNumberFilename = s"$outputDir/n.png"
  scanner.scanItemsNumber(itemsNumberFilename)
  val image = ImageIO.read(new File(itemsNumberFilename))
  val cells = extractor.extractInt(image)

  //  scrolling down to the last 35 artifacts doesn't work.
  //  Most likely it's only going to be fodder there, so don't bother for now
  val artifactsToSkip = 35
  scanner.scan(cells.get.get - artifactsToSkip)

  println("Done")
}
