package Entities

import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.TryValues.convertTryToSuccessOrFailure
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.util.Random

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
    val artifact = Artifact("Gambler", "Feather", 16, 4, "ATK",
      Map("ATK%" -> 20.4, "DEF%" -> 5.5, "HP%" -> 5.5, "DEF" -> 12))
    artifact.success.value.mainStatValue shouldBe 232
  }

  "Artifact constructor" should "return failure on invalid input" in {
    val artifact = Artifact("Gambler", "Flower", -1, -1, "NonExistentStat", Map())
    artifact.isFailure shouldBe true
  }

  "Validate" should "throw on level too low" in {
    val artifact = new Artifact("Gambler", "Flower", -1, 5, "HP", 5, Map("ATK" -> 12))
    Artifact.validate(artifact).isFailure shouldBe true
  }

  "Validate" should "throw on level too high" in {
    List(1, 2).foreach(rarity => {
      val minLevel = 5
      val level = minLevel + Random.nextInt(Int.MaxValue - minLevel)
      val artifact = new Artifact("Gambler", "Flower", level, rarity, "HP", 5, Map("ATK" -> 12))
      Artifact.validate(artifact).isFailure shouldBe true
    })
    List(3, 4, 5).foreach(rarity => {
      val minLevel = rarity * 4 + 1
      val level = minLevel + Random.nextInt(Integer.MAX_VALUE - minLevel)
      val artifact = new Artifact("Gambler", "Flower", level, rarity, "HP", 5, Map("ATK" -> 12))
      Artifact.validate(artifact).isFailure shouldBe true
    })
  }

  "Validate" should "throw on rarity too low" in {
    val rarity = Random.nextInt(Int.MaxValue).abs
    val artifact = new Artifact("Gambler", "Flower", 0, rarity, "HP", 5, Map("ATK" -> 12))
    Artifact.validate(artifact).isFailure shouldBe true
  }

  "Validate" should "throw on rarity too high" in {
    val maxRarity = 5
    val rarity = Random.nextInt(Int.MaxValue - maxRarity) + maxRarity
    val artifact = new Artifact("Gambler", "Flower", 0, rarity, "HP", 5, Map("ATK" -> 12))
    Artifact.validate(artifact).isFailure shouldBe true
  }

  "Validate" should "throw on number of sub stats too low" in {
    val subStats = Map[String, Double]("ATK" -> 12, "Elemental Mastery" -> 12, "DEF" -> 12, "DEF%" -> 5.5)

    //    1* and 2* artifacts can have 0 sub stats if they're on level lower than 4
    3 to 5 foreach (rarity => {
      val artifact = new Artifact("Gambler", "Flower", 0, rarity, "HP", 430, subStats.take(rarity - 3))
      Artifact.validate(artifact).isFailure shouldBe true
    })
  }

  "Validate" should "throw on number of sub stats too high" in {
    val subStats = Map[String, Double]("ATK" -> 12, "Elemental Mastery" -> 12,
      "DEF" -> 12, "DEF%" -> 5.5, "ATK%" -> 5.5)

    1 to 5 foreach (rarity => {
      val artifact = new Artifact("Gambler", "Flower", 0, rarity, "HP", 430, subStats.take(rarity))
      Artifact.validate(artifact).isFailure shouldBe true
    })
  }

  "Validate" should "do nothing on proper artifact" in {
    val artifact = new Artifact("Gambler", "Flower", 0, 3, "HP", 430, Map("ATK" -> 12))
    Artifact.validate(artifact)
  }
}
