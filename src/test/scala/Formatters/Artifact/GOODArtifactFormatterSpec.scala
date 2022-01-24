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
}

object GOODArtifactFormatterSpec {
  private val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0f, "Crit DMG%" -> 17.9f, "Crit RATE%" -> 3.5f, "HP%" -> 8.2f)).get
}
