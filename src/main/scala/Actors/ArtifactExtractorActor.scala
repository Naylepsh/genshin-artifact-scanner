package Actors

import Artifact.Artifact
import Extraction.ArtifactFromImageExtractable
import akka.actor.{Actor, Props}

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

class ArtifactExtractorActor(extractor: ArtifactFromImageExtractable) extends Actor {

  import ArtifactExtractorActor._

  override def receive: Receive = {
    case ExtractArtifact(source) =>
      val message = extractArtifact(source) match {
        case Success(artifact) => ArtifactExtractionSuccess(artifact)
        case Failure(exception) => ArtifactExtractionFailure(exception)
      }
      sender() ! message
  }

  private def extractArtifact(source: String) = {
    for {
      image <- openImage(source)
      artifact <- extractor.extractArtifact(image)
    } yield artifact
  }

  private def openImage(source: String): Try[BufferedImage] = Try {
    ImageIO.read(new File(source))
  }
}

object ArtifactExtractorActor {
  def props(extractor: ArtifactFromImageExtractable): Props = Props(new ArtifactExtractorActor(extractor))

  case class ExtractArtifact(source: String)

  case class ArtifactExtractionSuccess(artifact: Artifact)

  case class ArtifactExtractionFailure(failure: Throwable)
}
