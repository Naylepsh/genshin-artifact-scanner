package Artifact

import scala.io.Source

// TODO: calculate main stat value based on mainStat, level and rarity
case class Artifact(
                     setName: String,
                     slot: String,
                     level: Int,
                     rarity: Int,
                     mainStat: String,
                     subStats: Map[String, Float]) {
}

object Artifact {

  import play.api.libs.json._

  private val mainStats = Json.parse(Source.fromResource("mainStats.json").mkString)

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
}
