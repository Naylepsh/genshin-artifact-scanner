package Actors

import Actors.ArtifactScannerActor.{ArtifactScanned, ScanningComplete, StartScanning}
import Actors.Common.pathToExistingArtifact
import Scan.ArtifactScannable
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import java.awt.image.BufferedImage

class ArtifactScannerActorSpec extends TestKit(ActorSystem("ArtifactScannerActorSpec"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  import ArtifactScannerActorSpec._

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Artifact scanner" should {
    "send ArtifactScanned messages back to the sender" in {
      val cells = 1
      val actor = system.actorOf(ArtifactScannerActor.props(NullScanner()))

      actor ! StartScanning(cells)

      expectMsgType[ArtifactScanned]
    }

    "send ScanningComplete message back to the sender once scanning is complete" in {
      val cells = 1
      val actor = system.actorOf(ArtifactScannerActor.props(NullScanner()))

      actor ! StartScanning(cells)

      expectMsg(ScanningComplete)
    }
  }
}

object ArtifactScannerActorSpec {
  case class NullScanner() extends ArtifactScannable {
    override def scanRow(cells: Int): List[BufferedImage] = List(Common.openImage(pathToExistingArtifact).get)

    override def moveRowDown(amount: Int): Unit = ()
  }
}
