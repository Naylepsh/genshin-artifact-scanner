package Scan

import Capture.ScreenCapture.{RectangleCoordinates, captureRectangle}
import Utils.Image.ImageUtils.save

import java.awt.event.InputEvent.BUTTON1_DOWN_MASK
import java.awt.image.BufferedImage
import java.awt.{Point, Robot}
import scala.language.postfixOps


case class ArtifactScanner(workDir: String) extends ArtifactScannable {

  import ArtifactScanner._

  val robot = new Robot()

  def scanItemsNumber(filename: String): Unit = {
    val image = captureRectangle(itemsNumberCoordinates)
    save(image, filename)
  }

  def moveRowDown(amount: Int): Unit = {
    1 to amount foreach { _ => robot.mouseWheel(1) }
  }

  def scanRow(cells: Int = artifactsInRow): List[BufferedImage] = {
    val artifactPoints = 1 to cells map { i =>
      new Point(rowStartingCursorPosition.x + (i - 1) * cursorDeltaX, rowStartingCursorPosition.y)
    }

    artifactPoints.map(scanArtifact).toList
  }

  private def scanArtifact(point: Point): BufferedImage = {
    click(point)
    captureRectangle(artifactCoordinates)
  }

  private def click(point: Point): Unit = {
    val leftMouseButton = BUTTON1_DOWN_MASK
    robot.mouseMove(point.x, point.y)
    robot.mousePress(leftMouseButton)
    robot.mouseRelease(leftMouseButton)
  }
}

object ArtifactScanner {
  val cursorDeltaX = 150
  val artifactsInRow = 7
  //    Below is the optimal sequence of scroll amounts, that should hover around the middle of the artifact icon
  //    This has been tested on ~700 artifacts, and ended up being just a tiny bit above the middle.
  val scrollAmounts: List[Int] = 9 :: List.fill(12)(List(10, 10, 10, 9)).flatten
  private val rowStartingCursorPosition = new Point(260, 180)
  private val artifactCoordinates = RectangleCoordinates(new Point(1295, 120), new Point(1780, 670))
  private val itemsNumberCoordinates = RectangleCoordinates(new Point(1685, 35), new Point(1740, 60))
}
