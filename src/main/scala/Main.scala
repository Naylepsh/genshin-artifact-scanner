import Extract.{ArtifactExtract, TesseractWrapper}

import java.io.File
import javax.imageio.ImageIO

object Main extends App {
  //  val artifactCoordinates = RectangleCoordinates(new Point(1295, 120), new Point(1780, 670))
  //  val image = ScreenCapture.captureRectangle(artifactCoordinates)
  //  saveToFile(image, "./artifact")

  val dataPath = sys.env("TESSDATA")
  val language = "eng"
  val tesseract = TesseractWrapper(dataPath, language)

  val pathToFile = "F:/Code/artifact-helper/screen-capturer/artifact-0.png"
  val image = ImageIO.read(new File(pathToFile))
  val level = ArtifactExtract(tesseract).extractLevel(image)
  println(level)
  println("Done")
}
