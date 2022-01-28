package Entities

import Entities.Artifact.StatName.StatName

import scala.io.Source
import scala.util.{Failure, Try}

case class Artifact(setName: String, slot: String, level: Int, rarity: Int,
                    mainStat: StatName, mainStatValue: Double, subStats: Map[StatName, Double]) {
}

object Artifact {

  import play.api.libs.json._

  private val mainStats = Json.parse(Source.fromResource("mainStats.json").mkString)

  def apply(setName: String, slot: String, level: Int, rarity: Int,
            mainStat: StatName, subStats: Map[StatName, Double]): Try[Artifact] = {
    calcMainStatValue(mainStat, rarity, level) match {
      case Some(mainStatValue) =>
        Artifact(setName, slot, level, rarity, mainStat, mainStatValue, subStats)
      case None =>
        val message = s"Can't calculate main stat value due to invalid stat name=$mainStat, rarity=$rarity or level=$level"
        Failure(new RuntimeException(message))
    }
  }

  def apply(setName: String, slot: String, level: Int, rarity: Int,
            mainStat: StatName, mainStatValue: Double, subStats: Map[StatName, Double]): Try[Artifact] = {
    val cleanMainStatValue = trimAfterFirstDecimal(mainStatValue)
    val cleanSubStats = subStats.map { case (key, value) => key -> trimAfterFirstDecimal(value) }
    val artifact = new Artifact(setName, slot, level, rarity, mainStat, cleanMainStatValue, cleanSubStats)
    validate(artifact).map(_ => artifact)
  }

  def validate(artifact: Artifact): Try[Unit] = Try {
    val validators = List(validateLevel _, validateRarity _, validateSubStats _)
    validators.foreach(_ (artifact))
  }

  private def validateLevel(artifact: Artifact): Unit = {
    val minLevel = 0
    val maxLevel = 4 * artifact.rarity
    require(minLevel <= artifact.level && artifact.level <= maxLevel,
      s"Level=${artifact.level} outside of boundaries=[$minLevel, $maxLevel]")
  }

  private def validateRarity(artifact: Artifact): Unit = {
    val minRarity = 1
    val maxRarity = 5
    require(minRarity <= artifact.rarity && artifact.rarity <= maxRarity,
      s"Rarity=${artifact.rarity} outside of boundaries=[$minRarity, $maxRarity]")
  }

  private def validateSubStats(artifact: Artifact): Unit = {
    val offset = artifact.level / 4
    val baseMin = List(artifact.rarity - 2, 0).max
    val min = List(baseMin + offset, 4).min
    val baseMax = List(artifact.rarity - 1, 0).max
    val max = List(baseMax + offset, 4).min
    val subStats = artifact.subStats.size
    require(min <= subStats && subStats <= max, s"Number of substats=$subStats outside of boundaries=[$min, $max]")
  }

  private def trimAfterFirstDecimal(value: Double): Double =
    (math floor value * 10) / 10

  def calcMainStatValue(statName: StatName, rarity: Int, level: Int): Option[Double] = {
    // There is no 0* rarity, hence why - 1
    (mainStats \ convertStatName(statName.toString) \ (rarity - 1) \ level).asOpt[Double]
  }

  private def convertStatName(statName: String): String = {
    val isElementalDmg = List("Electro", "Hydro", "Pyro", "Cryo", "Geo", "Anemo").exists(statName.contains(_))
    if (isElementalDmg)
      "Elemental DMG Bonus%"
    else
      statName
  }

  object StatName extends Enumeration {
    type StatName = Value

    val atkFlat: StatName.Value = Value("ATK")
    val atkPercent: StatName.Value = Value("ATK%")
    val critDmgPercent: StatName.Value = Value("CRIT DMG%")
    val critRatePercent: StatName.Value = Value("CRIT Rate%")
    val defFlat: StatName.Value = Value("DEF")
    val defPercent: StatName.Value = Value("DEF%")
    val elementalMastery: StatName.Value = Value("Elemental Mastery")
    val energyRechargePercent: StatName.Value = Value("Energy Recharge%")
    val hpFlat: StatName.Value = Value("HP")
    val hpPercent: StatName.Value = Value("HP%")
    val electroDamagePercent: StatName.Value = Value("Electro DMG Bonus%")
    val hydroDamagePercent: StatName.Value = Value("Hydro DMG Bonus%")
    val pyroDamagePercent: StatName.Value = Value("Pyro DMG Bonus%")
    val cryoDamagePercent: StatName.Value = Value("Cryo DMG Bonus%")
    val anemoDamagePercent: StatName.Value = Value("Anemo DMG Bonus%")
    val geoDamagePercent: StatName.Value = Value("Geo DMG Bonus%")
    val dendroDamagePercent: StatName.Value = Value("Dendro DMG Bonus%")
    val physicalDamagePercent: StatName.Value = Value("Physical DMG Bonus%")
    val healingPercent: Artifact.StatName.Value = Value("Healing Bonus%")

    def fromString(string: String): Option[StatName.Value] =
      values.find(_.toString == string)
  }
}
