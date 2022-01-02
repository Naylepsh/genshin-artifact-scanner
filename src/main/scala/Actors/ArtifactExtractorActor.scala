package Actors

import Artifact.Artifact
import Extraction.ArtifactFromImageExtractor
import akka.actor.{Actor, Props}

import java.io.File
import javax.imageio.ImageIO
import scala.util.{Failure, Success, Try}

class ArtifactExtractorActor(extractor: ArtifactFromImageExtractor) extends Actor {

  import ArtifactExtractorActor._

  override def receive: Receive = {
    case ExtractArtifact(source) =>
      val artifactTry = for {
        image <- Try {
          ImageIO.read(new File(source))
        }
        artifact <- extractor.extractArtifact(image)
      } yield artifact

      val message = artifactTry match {
        case Success(artifact) => ArtifactExtractionSuccess(artifact)
        case Failure(exception) => ArtifactExtractionFailure(exception)
      }
      sender() ! message
  }
}

object ArtifactExtractorActor {
  def props(extractor: ArtifactFromImageExtractor): Props = Props(new ArtifactExtractorActor(extractor))

  case class ExtractArtifact(source: String)

  case class ArtifactExtractionSuccess(artifact: Artifact)

  case class ArtifactExtractionFailure(failure: Throwable)
}
