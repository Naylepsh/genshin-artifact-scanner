package Extract

import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.flatspec._
import org.scalatest.matchers._

class ArtifactStringExtractorSpec extends AnyFlatSpec with should.Matchers {

  implicit val floatEquality: Equality[Float] = TolerantNumerics.tolerantFloatEquality(tolerance = 1e-4f)

  "Extract stat value" should "extract integer-like value" in {
    ArtifactStringExtractor.extractStatValue("1234").value shouldBe 1234
  }

  "Extract stat value" should "extract ignore commas" in {
    ArtifactStringExtractor.extractStatValue("1,234").value shouldBe 1234
  }

  "Extract stat value" should "extract float-like value" in {
    ArtifactStringExtractor.extractStatValue("12.3%").value === 12.3
  }

  "Extract stat value" should "recognize that there are too many place after decimal" in {
    ArtifactStringExtractor.extractStatValue("1.234").value shouldBe 1234
  }
}
