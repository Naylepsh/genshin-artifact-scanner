package Extract

import Extract.ArtifactExtractorSpec._
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec._
import org.scalatest.matchers._

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class ArtifactExtractorSpec extends AnyFlatSpec with should.Matchers {

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
}

object ArtifactExtractorSpec {
  val dataPath: String = sys.env("TESSDATA")
  val language = "eng"
  val tesseract: TesseractWrapper = TesseractWrapper(dataPath, language)
  val extractor: ArtifactExtractor = ArtifactExtractor(tesseract)

  def getImage(pathToImage: String): BufferedImage =
    ImageIO.read(getClass.getResourceAsStream(pathToImage))

}
