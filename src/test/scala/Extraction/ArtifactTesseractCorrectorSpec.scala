package Extraction

import Extraction.ArtifactTesseractCorrector.correctSubStats
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ArtifactTesseractCorrectorSpec extends AnyFlatSpec with should.Matchers {
  "Correct sub stats" should "handle malformed data (l or t instead of numbers)" in {
    val rawData = "DEF+lt.0%"
    val expectedData = "DEF+11.0%"

    correctSubStats(rawData) shouldBe expectedData
  }

  "Correct sub stats" should "handle malformed data (H instead of numbers)" in {
    val rawData = "DEF+H.0%"
    val expectedData = "DEF+11.0%"

    correctSubStats(rawData) shouldBe expectedData
  }

  "Extract sub stats" should "handle malformed separator" in {
    val rawData = "DEFt11.0%"
    val expectedData = "DEF+11.0%"

    correctSubStats(rawData) shouldBe expectedData
  }
}
