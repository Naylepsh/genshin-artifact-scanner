package Extract

import Extract.ArtifactStringExtractor._
import Extract.ArtifactStringExtractorSpec.mkArtifactDescription
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

  "Extract sub first stat value" should "recognize that there are too many places after decimal" in {
    extractFirstStatValue("1.234").value shouldBe 1234
  }

  "Extract sub stat" should "pick up all values" in {
    val stats = List(("Energy Recharge", 11.0f), ("CRIT DMG", 17.9f), ("HP", 1234f), ("ATK", 12f))
    assertStatsGotExtracted(stats)
  }

  "Extract sub stat" should "handle both flat stat and % stat of the same name" in {
    val stats = List(("Energy Recharge", 11.0f), ("DEF%", 17.9f), ("HP", 1234f), ("HP%", 12.1f))
    assertStatsGotExtracted(stats)
  }

  def assertStatsGotExtracted(stats: List[(String, Float)]): Unit = {
    val rawData = mkArtifactDescription(stats)
    stats.foreach(stat => {
      val statName = stat._1
      extractSubStat(rawData, statName).value shouldBe stat
    })
  }

  "Extract sub stats" should "pick up all sub stats" in {
    val stats = List(("Energy Recharge", 11.0f), ("DEF%", 17.9f), ("HP", 1234f), ("HP%", 12.1f))
    val rawData = mkArtifactDescription(stats)
    extractSubStats(rawData).length shouldBe stats.length
  }
}

object ArtifactStringExtractorSpec {
  def mkArtifactDescription(stats: List[(String, Float)]): String =
    Random.shuffle(stats).map(statToStatString).mkString("\n")

  def statToStatString(stat: (String, Float)): String = {
    if (stat._1.endsWith("%"))
      s"${stat._1.dropRight(1)}+${stat._2}%"
    else
      s"${stat._1}+${stat._2}"
  }
}
