package Actors

import Artifact.Artifact
import Capture.ScreenCapture
import Capture.ScreenCapture.RectangleCoordinates
import Extraction.{ArtifactFromImageExtractable, NumberExtractable}
import Scan.ArtifactScannable
import akka.actor.{Actor, ActorLogging, Props}

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Try

class MasterActor(scanner: ArtifactScannable, extractor: ArtifactFromImageExtractable with NumberExtractable)
  extends Actor with ActorLogging {

  import ArtifactScannerActor._
  import MasterActor._

  private val extractorActor = context.actorOf(ArtifactExtractorActor.props(extractor))
  private val scannerActor = context.actorOf(ArtifactScannerActor.props(scanner, sys.env("OUTPUT_DIR")))
  private val itemsNumberCoordinates = RectangleCoordinates(new Point(1685, 35), new Point(1740, 60))

  override def receive: Receive = {
    case Start =>
      val numberTry = extractNumberOfItemsToScan()
      if (numberTry.isFailure)
        log.error("Failed to extract the number of artifacts")
      else {
        val artifactsToScan = numberTry.get
        context.become(receiveWithResults(List(), artifactsToScan))
        scannerActor ! StartScanning(artifactsToScan)
      }
    case ArtifactScanned(filename) => self ! ArtifactScanned(filename)
  }

  private def extractNumberOfItemsToScan(): Try[Int] = {
    extractor.extractInt(scanItemNumber())
  }

  private def scanItemNumber(): BufferedImage = {
    ScreenCapture.captureRectangle(itemsNumberCoordinates)
  }

  def receiveWithResults(artifacts: List[Artifact], artifactsExpected: Int): Receive = {
    case ArtifactScanned(filename) => log.info(s"Acquired $filename")
    case ScanningComplete => log.info("Done")
  }
}

object MasterActor {
  def props(scanner: ArtifactScannable, extractor: ArtifactFromImageExtractable with NumberExtractable): Props =
    Props(new MasterActor(scanner, extractor))

  object Start
}
