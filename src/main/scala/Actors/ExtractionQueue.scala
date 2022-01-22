package Actors

import Actors.ArtifactExtractorActor._
import Actors.ExtractionQueue.{Teardown, TeardownComplete}
import Extraction.{ArtifactFromImageExtractable, NumberExtractable}
import Utils.Image.ImageUtils.{open, save}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing.RoundRobinGroup
import org.apache.commons.io.FileUtils

import java.awt.image.BufferedImage
import java.io.File
import scala.util.{Failure, Success}

class ExtractionQueue(
                       extractors: List[ArtifactFromImageExtractable with NumberExtractable],
                       masterRef: ActorRef, workDir: String) extends Actor with ActorLogging {
  private val bufferedQueueLimit = 10
  private val extractorRouter = setupExtractorRouter()

  override def receive: Receive = receiver(List(), List(), availableWorkers = extractors.length)

  def receiver(bufferedQueue: List[BufferedImage], fileQueue: List[String], availableWorkers: Int): Receive = {
    case ExtractArtifact(image) =>
      extractArtifact(image, bufferedQueue, fileQueue, availableWorkers)
    case message: ArtifactExtractionResult =>
      sendToMasterAndCheckoutQueues(message, bufferedQueue, fileQueue, availableWorkers + 1)
    case Teardown =>
      teardown()
  }

  private def extractArtifact(image: BufferedImage, bufferedQueue: List[BufferedImage],
                              fileQueue: List[String], availableWorkers: Int): Unit = {
    if (availableWorkers > 0) {
      extractorRouter ! ExtractArtifact(image)
      context.become(receiver(bufferedQueue, fileQueue, availableWorkers - 1))
    } else if (bufferedQueue.length < bufferedQueueLimit)
      context.become(receiver(image :: bufferedQueue, fileQueue, availableWorkers))
    else {
      val dest = createFilename()
      save(image, dest)
      context.become(receiver(bufferedQueue, dest :: fileQueue, availableWorkers))
    }
  }

  private def sendToMasterAndCheckoutQueues(message: ArtifactExtractionResult, bufferedQueue: List[BufferedImage],
                                            fileQueue: List[String], availableWorkers: Int): Unit = {
    masterRef ! message

    if (bufferedQueue.nonEmpty) {
      context.become(receiver(bufferedQueue.tail, fileQueue, availableWorkers))
      self ! ExtractArtifact(bufferedQueue.head)
    }
    else if (fileQueue.nonEmpty) {
      context.become(receiver(bufferedQueue, fileQueue.tail, availableWorkers))
      open(fileQueue.head) match {
        case Success(image) => self ! ExtractArtifact(image)
        case Failure(exception) => log.error(s"Couldn't open file=${fileQueue.head} due to $exception")
      }
    } else {
      context.become(receiver(bufferedQueue, fileQueue, availableWorkers))
    }
  }

  private def teardown(): Unit = {
    FileUtils.deleteDirectory(new File(workDir))
    sender() ! TeardownComplete
  }

  private def setupExtractorRouter(): ActorRef = {
    val extractorActors = extractors.zipWithIndex.map {
      case (extractor, i) =>
        context.actorOf(ArtifactExtractorActor.props(extractor), s"extractor_$i")
    }
    context.actorOf(RoundRobinGroup(extractorActors.map(ref => ref.path.toString)).props())
  }

  private def createFilename(): String = {
    val id = java.util.UUID.randomUUID().toString
    s"$workDir/$id.png"
  }
}

object ExtractionQueue {
  def props(extractors: List[ArtifactFromImageExtractable with NumberExtractable],
            masterRef: ActorRef, workDir: String): Props =
    Props(new ExtractionQueue(extractors, masterRef, workDir))

  object Teardown

  object TeardownComplete
}