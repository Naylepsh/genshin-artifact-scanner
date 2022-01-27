package Extraction

import Entities.Artifact.StatNames._
import Extraction.ArtifactStringExtractor._
import Extraction.ArtifactStringExtractorSpec.mkArtifactDescription
import org.scalactic.{Equality, TolerantNumerics}
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.flatspec._
import org.scalatest.matchers._

import scala.util.Random

class ArtifactStringExtractorSpec extends AnyFlatSpec with should.Matchers {

  implicit val floatEquality: Equality[Float] = TolerantNumerics.tolerantFloatEquality(tolerance = 1e-4f)

  "Extract first stat value" should "extract integer-like value" in {
    extractFirstStatValue("1234").value shouldBe 1234
  }

  "Extract first stat value" should "extract ignore commas" in {
    extractFirstStatValue("1,234").value shouldBe 1234
  }

  "Extract sub first stat value" should "extract float-like value" in {
    extractFirstStatValue("12.3%").value === 12.3
  }

  "Extract sub stats" should "pick up all sub stats (including stats with same names but different types)" in {
    val stats = List((energyRechargePercent, 11.0f), (defPercent, 17.9f), (hpFlat, 1234f), (hpPercent, 12.1f))
    val rawData = mkArtifactDescription(stats)
    extractSubStats(rawData).length shouldBe stats.length
  }
}

object ArtifactStringExtractorSpec {
  def mkArtifactDescription(stats: List[(StatNames, Float)]): String =
    Random.shuffle(stats).map(statToStatString).mkString("\n")

  def statToStatString(stat: (StatNames, Float)): String = {
    val statName = stat._1.toString
    if (statName.endsWith("%"))
      s"${statName.dropRight(1)}+${stat._2}%"
    else
      s"$statName+${stat._2}"
  }
}
