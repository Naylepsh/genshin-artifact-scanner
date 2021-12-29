package Scan

import Capture.ScreenCapture.{RectangleCoordinates, captureRectangle, saveToFile}

import java.awt.event.InputEvent.BUTTON1_DOWN_MASK
import java.awt.{Point, Robot}
import scala.annotation.tailrec

case class ArtifactScanner(workDir: String) {

  import ArtifactScanner._

  val robot = new Robot()

  def scan(): Unit = {
    //    Below is the optimal sequence of scroll amounts, that should hover around the middle of the artifact icon
    //    This has been tested on ~700 artifacts, and ended up being just a tiny bit above the middle.
    val scrollAmounts = 9 :: List.fill(12)(List(10, 10, 10, 9)).flatten
    scan(110, scrollAmounts)
  }

  @tailrec
  private def scan(n: Int, scrollAmounts: List[Int]): Unit = {
    if (n > 0) {
      scanRow()
      moveRowDown(scrollAmounts.head)
      scan(n - 1, scrollAmounts.tail :+ scrollAmounts.head)
    }
  }

  private def moveRowDown(amount: Int): Unit = {
    1 to amount foreach { _ => robot.mouseWheel(1) }
  }

  private def scanRow(): Unit = {
    val filenames = 1 to artifactsInRow map { _ => createFilename() }
    val artifactPoints = 1 to artifactsInRow map { i =>
      new Point(rowStartingCursorPosition.x + (i - 1) * cursorDeltaX, rowStartingCursorPosition.y)
    }

    filenames zip artifactPoints foreach { x => scanArtifact(x._1)(x._2) }
  }

  private def scanArtifact(filename: String)(point: Point): Unit = {
    click(point)
    val image = captureRectangle(artifactCoordinates)
    saveToFile(image, filename)
  }

  private def click(point: Point): Unit = {
    val leftMouseButton = BUTTON1_DOWN_MASK
    robot.mouseMove(point.x, point.y)
    robot.mousePress(leftMouseButton)
    robot.mouseRelease(leftMouseButton)
  }

  private def createFilename(): String = {
    val format = "png"
    val id = java.util.UUID.randomUUID().toString
    s"$workDir/$id.$format"
  }
}

object ArtifactScanner {
  private val rowStartingCursorPosition = new Point(260, 180)
  private val cursorDeltaX = 150
  private val artifactsInRow = 7
  private val artifactCoordinates = RectangleCoordinates(new Point(1295, 120), new Point(1780, 670))
}
