package Capture

import java.awt.image.BufferedImage
import java.awt.{Point, Rectangle, Robot}
import java.io.File
import javax.imageio.ImageIO

object ScreenCapture {
  def captureRectangle(coordinates: RectangleCoordinates): BufferedImage = {
    val width = coordinates.bottomRight.x - coordinates.topLeft.x
    val height = coordinates.bottomRight.y - coordinates.topLeft.y
    captureRectangle(coordinates.topLeft.x, coordinates.topLeft.y, width, height)
  }

  def captureRectangle(x: Int, y: Int, width: Int, height: Int): BufferedImage = {
    val rectangle = new Rectangle(x, y, width, height)
    val image = new Robot().createScreenCapture(rectangle)
    image
  }

  def saveToFile(image: BufferedImage, dest: String, format: String = "png"): Unit =
    ImageIO.write(image, format, new File(enforceFormatOnFilename(dest, format)))

  private def enforceFormatOnFilename(filename: String, defaultFormat: String): String = {
    if (filename.endsWith(s".$defaultFormat"))
      filename
    else
      s"$filename.$defaultFormat"
  }

  case class RectangleCoordinates(topLeft: Point, bottomRight: Point)
}
