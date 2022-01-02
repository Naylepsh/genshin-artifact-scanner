package Actors

import Actors.ArtifactExtractorActor.{ArtifactExtractionFailure, ArtifactExtractionSuccess, ExtractArtifact}
import Artifact.Artifact
import Extraction.ArtifactFromImageExtractor
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
    "send back file opening failure" in {
      val error = new RuntimeException
      val extractor = FailingExtractor(error)
      val actor = system.actorOf(ArtifactExtractorActor.props(extractor))

      actor ! ExtractArtifact("/non-existent-directory/non-existent-file.png")

      expectMsgType[ArtifactExtractionFailure]
    }

    "send back artifact extraction failure" in {
      val error = new RuntimeException
      val extractor = FailingExtractor(error)
      val actor = system.actorOf(ArtifactExtractorActor.props(extractor))

      actor ! ExtractArtifact(pathToExistingArtifact)

      expectMsg(ArtifactExtractionFailure(error))
    }

    "send back artifact" in {
      val artifact = Artifact(
        setName = "Gambler", slot = "Flower", level = 20,
        mainStat = ("Baz", 1234), subStats = Map()
      )
      val extractor = SucceedingExtractor(artifact)
      val actor = system.actorOf(ArtifactExtractorActor.props(extractor))

      actor ! ExtractArtifact(pathToExistingArtifact)

      expectMsg(ArtifactExtractionSuccess(artifact))
    }
  }
}

object ArtifactExtractorActorSpec {
  val pathToExistingArtifact: String = getClass.getResource("/artifacts/4-star-2-stats-plume.png").getPath

  case class FailingExtractor(error: Throwable) extends ArtifactFromImageExtractor {
    override def extractArtifact(image: BufferedImage): Try[Artifact] = Failure(error)
  }

  case class SucceedingExtractor(artifact: Artifact) extends ArtifactFromImageExtractor {
    override def extractArtifact(image: BufferedImage): Try[Artifact] = Success(artifact)
  }
}