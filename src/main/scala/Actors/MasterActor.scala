package Actors

import Actors.ExtractionQueue.{Teardown, TeardownComplete}
import Artifact.Artifact
import Capture.ScreenCapture
import Capture.ScreenCapture.RectangleCoordinates
import Extraction.{ArtifactFromImageExtractable, ItemExtractor, NumberExtractable}
import Scan.ArtifactScannable
import Utils.Logging.ImageLogger
import akka.actor.{Actor, ActorLogging, Props}

import java.awt.Point
import java.awt.image.BufferedImage
import scala.util.Try

class MasterActor(scanner: ArtifactScannable, extractors: List[ArtifactFromImageExtractable with NumberExtractable])
  extends Actor with ActorLogging {

  import ArtifactExtractorActor._
  import ArtifactScannerActor._
  import MasterActor._

  private val workDir = sys.env("OUTPUT_DIR")
  private val scannerActor = context.actorOf(ArtifactScannerActor.props(scanner))
  private val itemsNumberCoordinates = RectangleCoordinates(new Point(1685, 35), new Point(1740, 60))
  private val extractionQueue = context.actorOf(ExtractionQueue.props(extractors, self, s"$workDir/temp"))
  private val logImage = ImageLogger.log(s"$workDir/logs") _

  override def receive: Receive = {
    /**
     * It's possible that the master actor got the message from scanner before it managed to change its context.
     * In such cases simply putting the message back on the queue hoping that the context changes before it arrives
     * should do the trick.
     *
     * Something like `case other: ArtifactScanned | ScanningComplete => self ! other` would be cleaner,
     * but the compiler complains.
     */
    case Start => start()
    case other => putBackOnQueue(other)
  }

  def receiveWithResults(artifacts: List[Artifact], artifactsExpected: Int): Receive = {
    case ArtifactScanned(image) =>
      extractionQueue ! ExtractArtifact(image)
    case ArtifactExtractionSuccess(artifact, _) =>
      log.info(s"${artifactsExpected - 1} artifacts left to extract.")
      context.become(receiveWithResults(artifact :: artifacts, artifactsExpected - 1))
      if (artifactsExpected == 1)
        extractionQueue ! Teardown
    case ArtifactExtractionFailure(failure, image) =>
      val message = s"Failed artifact extraction due to $failure"
      log.error(message)
      logImage(image, message)
      context.become(receiveWithResults(artifacts, artifactsExpected - 1))
      if (artifactsExpected == 1)
        extractionQueue ! Teardown
    case ScanningComplete => // This doesn't mean that the extraction is complete though
    case TeardownComplete => log.info("Done")
  }

  private def start(): Unit = {
    val numberTry = extractNumberOfItemsToScan()
    if (numberTry.isFailure) {
      log.error("Failed to extract the number of artifacts")
    } else {
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
    /**
     * Scrolling down on the last couple of rows doesn't work.
     * Those rows are bound to be fodder anyway, so it's not an issue if they do not get scanned.
     * TODO: It would be good to handle them at some point though.
     */
    val artifactsToSkip = 35
    ItemExtractor.extractNumberOfItems(extractors.head)(scanItemNumber()).map(_ - artifactsToSkip)
  }

  private def scanItemNumber(): BufferedImage =
    ScreenCapture.captureRectangle(itemsNumberCoordinates)
}

object MasterActor {
  def props(scanner: ArtifactScannable, extractors: List[ArtifactFromImageExtractable with NumberExtractable]): Props =
    Props(new MasterActor(scanner, extractors))


  object Start
}
