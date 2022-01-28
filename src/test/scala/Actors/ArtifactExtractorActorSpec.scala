package Actors

import Actors.ArtifactExtractorActor.{ArtifactExtractionFailure, ArtifactExtractionSuccess, ExtractArtifact}
import Common.Common.{openImage, pathToExistingArtifact}
import Entities.Artifact
import Entities.Artifact.SetName.gambler
import Entities.Artifact.StatName.atkFlat
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
      val image = openImage(pathToExistingArtifact).get

      actor ! ExtractArtifact(image)

      expectMsg(ArtifactExtractionFailure(error, image))
    }

    "send back artifact" in {
      val artifact = new Artifact(
        setName = gambler, slot = "Feather", level = 20, rarity = 5,
        mainStat = atkFlat, mainStatValue = 311, subStats = Map()
      )
      val extractor = SucceedingExtractor(artifact)
      val actor = system.actorOf(ArtifactExtractorActor.props(extractor))
      val image = openImage(pathToExistingArtifact).get

      actor ! ExtractArtifact(image)

      expectMsg(ArtifactExtractionSuccess(artifact, image))
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