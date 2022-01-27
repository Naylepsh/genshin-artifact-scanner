package Entities

import Entities.Artifact.StatNames.StatNames

import scala.io.Source
import scala.util.{Failure, Try}

case class Artifact(setName: String, slot: String, level: Int, rarity: Int,
                    mainStat: String, mainStatValue: Double, subStats: Map[StatNames, Double]) {
}

object Artifact {

  import play.api.libs.json._

  private val mainStats = Json.parse(Source.fromResource("mainStats.json").mkString)

  def apply(setName: String, slot: String, level: Int, rarity: Int,
            mainStat: String, subStats: Map[StatNames, Double]): Try[Artifact] = {
    calcMainStatValue(mainStat, rarity, level) match {
      case Some(mainStatValue) =>
        Artifact(setName, slot, level, rarity, mainStat, mainStatValue, subStats)
      case None =>
        val message = s"Can't calculate main stat value due to invalid stat name=$mainStat, rarity=$rarity or level=$level"
        Failure(new RuntimeException(message))
    }
  }

  def apply(setName: String, slot: String, level: Int, rarity: Int,
            mainStat: String, mainStatValue: Double, subStats: Map[StatNames, Double]): Try[Artifact] = {
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

  def calcMainStatValue(statName: String, rarity: Int, level: Int): Option[Double] = {
    // There is no 0* rarity, hence why - 1
    (mainStats \ convertStatName(statName) \ (rarity - 1) \ level).asOpt[Double]
  }

  private def convertStatName(statName: String): String = {
    val isElementalDmg = List("Electro", "Hydro", "Pyro", "Cryo", "Geo", "Anemo").exists(statName.contains(_))
    if (isElementalDmg)
      "Elemental DMG Bonus%"
    else
      statName
  }

  object StatNames extends Enumeration {
    type StatNames = Value

    val atkFlat: StatNames.Value = Value("ATK")
    val atkPercent: StatNames.Value = Value("ATK%")
    val critDmgPercent: StatNames.Value = Value("CRIT DMG%")
    val critRatePercent: StatNames.Value = Value("CRIT Rate%")
    val defFlat: StatNames.Value = Value("DEF")
    val defPercent: StatNames.Value = Value("DEF%")
    val elementalMastery: StatNames.Value = Value("Elemental Mastery")
    val energyRechargePercent: StatNames.Value = Value("Energy Recharge%")
    val hpFlat: StatNames.Value = Value("HP")
    val hpPercent: StatNames.Value = Value("HP%")

    def fromString(string: String): Option[StatNames.Value] =
      values.find(_.toString == string)
  }
}
