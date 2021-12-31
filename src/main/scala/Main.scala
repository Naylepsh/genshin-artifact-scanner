import Extraction._
import Scan.ArtifactScanner

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
  scanner.scan(770)

  println("Done")
}
