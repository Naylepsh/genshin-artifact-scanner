package Extract

import Extract.ArtifactImageExtractorSpec._
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

  "Extract set name" should "extract exact set name" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractSetName(image).success.value shouldBe "Husk of Opulent Dreams"
  }

  "Extract main stat" should "extract exact stat name and value" in {
    val pathToFile = "/artifacts/artifact-0.png"
    val image = getImage(pathToFile)
    extractor.extractMainStat(image).success.value shouldBe("HP", 4780)
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
