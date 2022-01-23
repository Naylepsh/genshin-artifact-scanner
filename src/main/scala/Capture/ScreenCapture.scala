package Capture

import java.awt.image.BufferedImage
import java.awt.{Point, Rectangle, Robot}

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

  case class RectangleCoordinates(topLeft: Point, bottomRight: Point) {
    val width: Int = bottomRight.x - topLeft.x
    val height: Int = bottomRight.y - topLeft.y
  }
}
