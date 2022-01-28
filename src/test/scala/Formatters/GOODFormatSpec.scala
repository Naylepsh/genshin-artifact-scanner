package Formatters

import Entities.Artifact
import Entities.Artifact.SetName.huskOfOpulentDreams
import Entities.Artifact.StatName._
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
    setName = huskOfOpulentDreams, slot = "Flower", level = 20, rarity = 5, mainStat = hpFlat,
    subStats = Map(energyRechargePercent -> 11.0f, critDmgPercent -> 17.9f,
      critRatePercent -> 3.5f, hpPercent -> 8.2f)).get
}