package Extraction

import Extraction.ArtifactImageExtractorSpec._
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec._
import org.scalatest.matchers._

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ArtifactImageExtractorSpec extends AnyFlatSpec with should.Matchers {

  "Extract level" should "extract exact level" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractLevel(image).success.value shouldBe 20
  }

  "Extract set name" should "extract the name from 4 sub stats artifact" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe "Husk of Opulent Dreams"
  }

  "Extract set name" should "extract the name from 3 sub stats artifact" in {
    val pathToFile = "/artifacts/5-star-3-stats.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe "Heart of Depth"
  }

  "Extract set name" should "extract the name from 2 sub stats artifact" in {
    val pathToFile = "/artifacts/4-star-2-stats.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe "Gambler"
  }

  "Extract main stat" should "extract exact stat name and value" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractMainStat(image).success.value shouldBe("HP", 4780)
  }

  "Extract rarity" should "extract 5*" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractRarity(image) shouldBe 5
  }

  "Extract sub stats number" should "return 4 on an artifact with 4 sub stats" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractSubStatsNumber(image) shouldBe 4
  }

  "Extract sub stats number" should "return 3 on an artifact with 3 sub stats" in {
    val pathToFile = "/artifacts/5-star-3-stats.png"
    val image = getImage(pathToFile)
    extractor.extractSubStatsNumber(image) shouldBe 3
  }

  "Extract sub stats number" should "return 2 on an artifact with 2 sub stats" in {
    val pathToFile = "/artifacts/4-star-2-stats.png"
    val image = getImage(pathToFile)
    extractor.extractSubStatsNumber(image) shouldBe 2
  }
}

object ArtifactImageExtractorSpec {
  val dataPath: String = sys.env("TESSDATA")
  val language = "eng"
  val tesseract: TesseractWrapper = TesseractWrapper(dataPath, language)
  val extractor: ArtifactImageExtractor = ArtifactImageExtractor(tesseract)

  def getImage(pathToImage: String): BufferedImage =
    ImageIO.read(getClass.getResourceAsStream(pathToImage))

}
