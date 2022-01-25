package Formatters

import Artifact.Artifact
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GOODFormatSpec extends AnyFlatSpec with should.Matchers {

  import GOODFormat._
  import GOODFormatSpec._

  "Format" should "capitalize set name and remove whitespace under setKey" in {
    GOODArtifact(artifact).setKey shouldBe "HuskOfOpulentDreams"
  }

  "Format" should "lowercase slot name and keep only the first word under slotKey" in {
    GOODArtifact(artifact).slotKey shouldBe "flower"
  }

  "Format" should "save level under level" in {
    GOODArtifact(artifact).level shouldBe artifact.level
  }

  "Format" should "save rarity under rarity" in {
    GOODArtifact(artifact).rarity shouldBe artifact.rarity
  }

  "Format" should "save main stat name under mainStatKey" in {
    GOODArtifact(artifact).mainStatKey shouldBe "hp"
  }

  "Format" should "add viable placeholders for location and lock" in {
    val formatted = GOODArtifact(artifact)
    formatted.location shouldBe ""
    formatted.lock shouldBe false
  }

  "Format" should "save sub stats under substats" in {
    val subStats = GOODArtifact(artifact).substats
    subStats.length shouldBe artifact.subStats.size
  }
}

object GOODFormatSpec {
  private val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0f, "CRIT DMG%" -> 17.9f, "CRIT Rate%" -> 3.5f, "HP%" -> 8.2f)).get
}