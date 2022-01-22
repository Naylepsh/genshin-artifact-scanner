package Actors

import Scan.ArtifactScannable
import Scan.ArtifactScanner.{artifactsInRow, scrollAmounts}
import akka.actor.{Actor, Props}

import java.awt.image.BufferedImage
import scala.annotation.tailrec
import scala.language.postfixOps

class ArtifactScannerActor(scanner: ArtifactScannable) extends Actor {

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

    scanner.scanRow(cellsToScan).foreach(sender() ! ArtifactScanned(_))
  }
}

object ArtifactScannerActor {
  def props(scanner: ArtifactScannable): Props = Props(new ArtifactScannerActor(scanner))

  case class StartScanning(cells: Int)

  case class ArtifactScanned(image: BufferedImage)

  case object ScanningComplete
}
