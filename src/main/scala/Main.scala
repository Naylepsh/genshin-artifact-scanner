import Extract.ArtifactExtract

import java.io.File
import javax.imageio.ImageIO

object Main extends App {
  //  val artifactCoordinates = RectangleCoordinates(new Point(1295, 120), new Point(1780, 670))
  //  val image = ScreenCapture.captureRectangle(artifactCoordinates)
  //  saveToFile(image, "./artifact")

  val pathToFile = "F:/Code/artifact-helper/screen-capturer/artifact-0.png"
  val image = ImageIO.read(new File(pathToFile))
  val level = ArtifactExtract.extractLevel(image)
  println(level)
  println("Done")
}
