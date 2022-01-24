package Formatters.Artifact

import Artifact.Artifact
import Formatters.Artifact.GOODArtifactFormatter.format
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GOODArtifactFormatterSpec extends AnyFlatSpec with should.Matchers {

  import GOODArtifactFormatterSpec._

  "Format" should "Capitalize set name and remove whitespace under setKey" in {
    format(artifact)("setKey") shouldBe "HuskOfOpulentDreams"
  }
}

object GOODArtifactFormatterSpec {
  private val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0f, "Crit DMG%" -> 17.9f, "Crit RATE%" -> 3.5f, "HP%" -> 8.2f)).get
}
