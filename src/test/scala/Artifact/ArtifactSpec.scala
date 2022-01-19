package Artifact

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ArtifactSpec extends AnyFlatSpec with should.Matchers {
  "Calc main stat" should "calculate value for valid stat, rarity and level" in {
    Artifact.calcMainStatValue("HP", 5, 20).value shouldBe 4780
  }

  "Calc main stat" should "return None on invalid stat" in {
    Artifact.calcMainStatValue("Invalid stat name", 5, 20) shouldBe None
  }

  "Calc main stat" should "return None on invalid rarity" in {
    Artifact.calcMainStatValue("HP", 0, 20) shouldBe None
  }

  "Calc main stat" should "return None on invalid level" in {
    Artifact.calcMainStatValue("HP", 4, 20) shouldBe None
  }

  "Calc main stat" should "handle elemental stat name" in {
    val elementalStats = List("Electro", "Hydro", "Pyro", "Cryo", "Geo", "Anemo")
    elementalStats foreach (statName => {
      Artifact.calcMainStatValue(statName, 5, 20).value shouldBe 46.6
    })
  }

  "Artifact constructor" should "calculate main stat value" in {
    val artifact = Artifact("Gambler", "Flower", 16, 4, "ATK", Map())
    artifact.success.value.mainStatValue shouldBe 232
  }

  "Artifact constructor" should "return failure on invalid input" in {
    val artifact = Artifact("Gambler", "Flower", -1, -1, "NonExistentStat", Map())
    artifact.isFailure shouldBe true
  }
}
