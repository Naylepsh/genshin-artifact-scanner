import Capture.ScreenCapture
import Capture.ScreenCapture._

import java.awt.Point

object Main extends App {
  val artifactCoordinates = RectangleCoordinates(new Point(1295, 120), new Point(1780, 670))
  val image = ScreenCapture.captureRectangle(artifactCoordinates)
  saveToFile(image, "./artifact")
  println("Done")
}
