package Actors

import Scan.ArtifactScannable
import Scan.ArtifactScanner.{artifactsInRow, scrollAmounts}
import akka.actor.{Actor, Props}

import scala.annotation.tailrec
import scala.language.postfixOps

class ArtifactScannerActor(scanner: ArtifactScannable, workDir: String) extends Actor {

  import ArtifactScannerActor._

  override def receive: Receive = {
    case StartScanning(cells) => startScanning(cells)
  }

  private def startScanning(cells: Int): Unit = {
    scan(cells, scrollAmounts)
    sender() ! ScanningComplete
  }

  @tailrec
  private def scan(cells: Int, scrollAmounts: List[Int]): Unit = {
    if (cells > 0) {
      scanRow(cells)
    }

    if (cells > artifactsInRow) {
      scanner.moveRowDown(scrollAmounts.head)
      scan(cells - artifactsInRow, scrollAmounts.tail :+ scrollAmounts.head)
    }
  }

  private def scanRow(cells: Int): Unit = {
    val cellsToScan = List(cells, artifactsInRow).min
    val filenames = createFilenames(cellsToScan)

    scanner.scanRow(cellsToScan, filenames)

    filenames.foreach(sender() ! ArtifactScanned(_))
  }

  private def createFilenames(n: Int): List[String] = 1 to n map { _ => createFilename() } toList

  private def createFilename(): String = {
    val format = "png"
    val id = java.util.UUID.randomUUID().toString
    s"$workDir/$id.$format"
  }

}

object ArtifactScannerActor {
  def props(scanner: ArtifactScannable, workDir: String): Props = Props(new ArtifactScannerActor(scanner, workDir))

  case class StartScanning(cells: Int)

  case class ArtifactScanned(filename: String)

  case object ScanningComplete
}
