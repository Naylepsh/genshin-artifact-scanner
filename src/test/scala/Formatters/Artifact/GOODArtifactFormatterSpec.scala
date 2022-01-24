package Formatters.Artifact

import Artifact.Artifact
import Formatters.Artifact.GOODArtifactFormatter.format
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GOODArtifactFormatterSpec extends AnyFlatSpec with should.Matchers {

  import GOODArtifactFormatterSpec._

  "Format" should "capitalize set name and remove whitespace under setKey" in {
    format(artifact)("setKey") shouldBe "HuskOfOpulentDreams"
  }

  "Format" should "lowercase slot name and keep only the first word under slotKey" in {
    format(artifact)("slotKey") shouldBe "flower"
  }

  "Format" should "save level under level" in {
    format(artifact)("level") shouldBe artifact.level
  }

  "Format" should "save rarity under rarity" in {
    format(artifact)("rarity") shouldBe artifact.rarity
  }

  "Format" should "save main stat name under mainStatKey" in {
    format(artifact)("mainStatKey") shouldBe "hp"
  }

  "Format" should "add viable placeholders for location and lock" in {
    val formatted = format(artifact)
    formatted("location") shouldBe ""
    formatted("lock") shouldBe false
  }

  "Format" should "save sub stats under substats" in {
    val subStats = format(artifact)("substats").asInstanceOf[List[Map[String, Any]]]
    subStats.length shouldBe artifact.subStats.size
  }
}

object GOODArtifactFormatterSpec {
  private val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0f, "Crit DMG%" -> 17.9f, "Crit RATE%" -> 3.5f, "HP%" -> 8.2f)).get
}
