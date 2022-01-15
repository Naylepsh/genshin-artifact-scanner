package Actors

import Actors.ArtifactExtractorActor.{ArtifactExtractionFailure, ArtifactExtractionSuccess, ExtractArtifact}
import Actors.Common.{openImage, pathToExistingArtifact}
import Artifact.Artifact
import Extraction.ArtifactFromImageExtractable
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import java.awt.image.BufferedImage
import scala.util.{Failure, Success, Try}

class ArtifactExtractorActorSpec extends TestKit(ActorSystem("ArtifactExtractorActorSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Artifact extractor" should {
    import ArtifactExtractorActorSpec._
    "send back artifact extraction failure" in {
      val error = new RuntimeException
      val extractor = FailingExtractor(error)
      val actor = system.actorOf(ArtifactExtractorActor.props(extractor))

      openImage(pathToExistingArtifact).map(actor ! ExtractArtifact(_))

      expectMsg(ArtifactExtractionFailure(error))
    }

    "send back artifact" in {
      val artifact = Artifact(
        setName = "Gambler", slot = "Flower", level = 20,
        mainStat = ("Baz", 1234), subStats = Map()
      )
      val extractor = SucceedingExtractor(artifact)
      val actor = system.actorOf(ArtifactExtractorActor.props(extractor))

      openImage(pathToExistingArtifact).map(actor ! ExtractArtifact(_))

      expectMsg(ArtifactExtractionSuccess(artifact))
    }
  }
}

object ArtifactExtractorActorSpec {
  case class FailingExtractor(error: Throwable) extends ArtifactFromImageExtractable {
    override def extractArtifact(image: BufferedImage): Try[Artifact] = Failure(error)
  }

  case class SucceedingExtractor(artifact: Artifact) extends ArtifactFromImageExtractable {
    override def extractArtifact(image: BufferedImage): Try[Artifact] = Success(artifact)
  }
}