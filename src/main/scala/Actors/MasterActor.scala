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

  import ArtifactExtractorActor._
  import ArtifactScannerActor._
  import MasterActor._

  private val extractorActor = context.actorOf(ArtifactExtractorActor.props(extractor))
  private val scannerActor = context.actorOf(ArtifactScannerActor.props(scanner, sys.env("OUTPUT_DIR")))
  private val itemsNumberCoordinates = RectangleCoordinates(new Point(1685, 35), new Point(1740, 60))

  override def receive: Receive = {
    /**
     * It's possible that the master actor got the message from scanner before it managed to change its context.
     * In such cases simply putting the message back on the queue hoping that the context changes before it arrives
     * should do the trick.
     *
     * Something like `case other: ArtifactScanned => self ! other` would be cleaner, but the compiler complains.
     */
    case Start => start()
    case ArtifactScanned(filename) => putBackOnQueue(ArtifactScanned(filename))
    case other => putBackOnQueue(other)
  }

  def receiveWithResults(artifacts: List[Artifact], artifactsExpected: Int): Receive = {
    case ArtifactScanned(filename) =>
      extractorActor ! ExtractArtifact(filename)
    case ArtifactExtractionSuccess(artifact) =>
      log.info(s"${artifactsExpected - 1} artifacts left to extract.")
      context.become(receiveWithResults(artifact :: artifacts, artifactsExpected - 1))
    case ArtifactExtractionFailure(failure) =>
      log.error(s"Failed artifact extraction due to $failure")
      log.info(s"${artifactsExpected - 1} artifacts left to extract.")
      context.become(receiveWithResults(artifacts, artifactsExpected - 1))
    case ScanningComplete =>
      log.info("Done")
  }

  private def start(): Unit = {
    val numberTry = extractNumberOfItemsToScan()
    if (numberTry.isFailure)
      log.error("Failed to extract the number of artifacts")
    else {
      val artifactsToScan = numberTry.get
      log.info(s"Attempting to scan $artifactsToScan artifacts")
      context.become(receiveWithResults(List(), artifactsToScan))
      scannerActor ! StartScanning(artifactsToScan)
    }
  }

  private def putBackOnQueue(message: Any): Unit = {
    self ! message
  }

  private def extractNumberOfItemsToScan(): Try[Int] = {
    extractor.extractInt(scanItemNumber())
  }

  private def scanItemNumber(): BufferedImage = {
    ScreenCapture.captureRectangle(itemsNumberCoordinates)
  }
}

object MasterActor {
  def props(scanner: ArtifactScannable, extractor: ArtifactFromImageExtractable with NumberExtractable): Props =
    Props(new MasterActor(scanner, extractor))

  object Start
}
