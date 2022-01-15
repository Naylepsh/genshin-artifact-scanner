package Actors

import Artifact.Artifact
import Extraction.ArtifactFromImageExtractable
import akka.actor.{Actor, Props}

import java.awt.image.BufferedImage
import scala.util.{Failure, Success}

class ArtifactExtractorActor(extractor: ArtifactFromImageExtractable) extends Actor {

  import ArtifactExtractorActor._

  override def receive: Receive = {
    case ExtractArtifact(image) =>
      val message = extractor.extractArtifact(image) match {
        case Success(artifact) => ArtifactExtractionSuccess(artifact)
        case Failure(exception) => ArtifactExtractionFailure(exception)
      }
      sender() ! message
  }
}

object ArtifactExtractorActor {
  def props(extractor: ArtifactFromImageExtractable): Props = Props(new ArtifactExtractorActor(extractor))

  case class ExtractArtifact(image: BufferedImage)

  case class ArtifactExtractionSuccess(artifact: Artifact)

  case class ArtifactExtractionFailure(failure: Throwable)
}
