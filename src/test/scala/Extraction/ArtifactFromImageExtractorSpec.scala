package Extraction

import Entities.Artifact
import Entities.Artifact.SetName._
import Entities.Artifact.StatName._
import Extraction.ArtifactFromImageExtractorSpec._
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec._
import org.scalatest.matchers._

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ArtifactFromImageExtractorSpec extends AnyFlatSpec with should.Matchers {

  "Extract level" should "extract exact level" in {
    val pathToFile = "/artifacts/5-star-4-stats-flower.png"
    val image = getImage(pathToFile)
    extractor.extractLevel(image).success.value shouldBe 20
  }

  "Extract set name" should "extract the name from 4 sub stats artifact" in {
    val pathToFile = "/artifacts/5-star-4-stats-flower.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe huskOfOpulentDreams
  }

  "Extract set name" should "extract the name from 3 sub stats artifact" in {
    val pathToFile = "/artifacts/5-star-3-stats-sands.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe heartOfDepth
  }

  "Extract set name" should "extract the name from 2 sub stats artifact" in {
    val pathToFile = "/artifacts/4-star-2-stats-plume.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe gambler
  }

  "Extract main stat" should "extract exact stat name and value" in {
    val pathToFile = "/artifacts/5-star-4-stats-flower.png"
    val image = getImage(pathToFile)
    extractor.extractMainStat(image).success.value shouldBe hpFlat
  }

  "Extract rarity" should "extract 5*" in {
    val pathToFile = "/artifacts/5-star-4-stats-flower.png"
    val image = getImage(pathToFile)
    extractor.extractRarity(image) shouldBe 5
  }

  "Extract sub stats number" should "return 4 on an artifact with 4 sub stats" in {
    val pathToFile = "/artifacts/5-star-4-stats-flower.png"
    val image = getImage(pathToFile)
    extractor.extractSubStatsNumber(image) shouldBe 4
  }

  "Extract sub stats number" should "return 3 on an artifact with 3 sub stats" in {
    val pathToFile = "/artifacts/5-star-3-stats-sands.png"
    val image = getImage(pathToFile)
    extractor.extractSubStatsNumber(image) shouldBe 3
  }

  "Extract sub stats number" should "return 2 on an artifact with 2 sub stats" in {
    val pathToFile = "/artifacts/4-star-2-stats-plume.png"
    val image = getImage(pathToFile)
    extractor.extractSubStatsNumber(image) shouldBe 2
  }

  "Extract slot" should "extract plume" in {
    val pathToFile = "/artifacts/4-star-2-stats-plume.png"
    val image = getImage(pathToFile)
    extractor.extractSlot(image).success.value shouldBe "Plume"
  }

  "Extract slot" should "extract sands" in {
    val pathToFile = "/artifacts/5-star-3-stats-sands.png"
    val image = getImage(pathToFile)
    extractor.extractSlot(image).success.value shouldBe "Sands"
  }

  "Extract slot" should "extract flower" in {
    val pathToFile = "/artifacts/5-star-4-stats-flower.png"
    val image = getImage(pathToFile)
    extractor.extractSlot(image).success.value shouldBe "Flower"
  }

  "Extract slot" should "extract goblet" in {
    val pathToFile = "/artifacts/5-star-4-stats-goblet.png"
    val image = getImage(pathToFile)
    extractor.extractSlot(image).success.value shouldBe "Goblet"
  }

  "Extract slot" should "extract circlet" in {
    val pathToFile = "/artifacts/5-star-4-stats-circlet.png"
    val image = getImage(pathToFile)
    extractor.extractSlot(image).success.value shouldBe "Circlet"
  }

  "Extract artifact" should "extract all artifact data from a valid image" in {
    val pathToFile = "/artifacts/5-star-4-stats-goblet.png"
    val image = getImage(pathToFile)

    val artifact = extractor.extractArtifact(image)

    val expectedArtifact = new Artifact(setName = lavawalker, slot = "Goblet", level = 4, rarity = 5,
      mainStat = electroDamagePercent, mainStatValue = 14.9, subStats = Map(
        hpFlat -> 269,
        defFlat -> 19,
        critDmgPercent -> 7.0f,
        elementalMastery -> 16
      )
    )
    artifact.success.value shouldBe expectedArtifact
  }
}

object ArtifactFromImageExtractorSpec {
  val dataPath: String = sys.env("TESSDATA")
  val language = "eng"
  val tesseract: TesseractWrapper = TesseractWrapper(dataPath, language)
  val extractor: ArtifactTesseractExtractor = ArtifactTesseractExtractor(tesseract)

  def getImage(pathToImage: String): BufferedImage =
    ImageIO.read(getClass.getResourceAsStream(pathToImage))
}
