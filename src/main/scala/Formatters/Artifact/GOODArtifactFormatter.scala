package Formatters.Artifact

import Artifact.Artifact

object GOODArtifactFormatter extends ArtifactFormatter {
  private val statNameMap = Map[String, String](
    "HP" -> "hp",
    "HP%" -> "hp_",
    "ATK" -> "atk",
    "ATK%" -> "atk_",
    "DEF" -> "def",
    "DEF%" -> "def_",
    "Elemental Mastery" -> "eleMas",
    "Energy Recharge" -> "enerRech_",
    "Healing Bonus%" -> "heal_",
    "CRIT Rate%" -> "critRate_",
    "CRIT DMG%" -> "critDMG_",
    "Physical DMG Bonus%" -> "physical_dmg_",
    "Anemo DMG Bonus%" -> "anemo_dmg_",
    "Geo DMG Bonus%" -> "geo_dmg_",
    "Electro DMG Bonus%" -> "electro_dmg_",
    "Hydro DMG Bonus%" -> "hydro_dmg_",
    "Pyro DMG Bonus%" -> "pyro_dmg_",
    "Cryo DMG Bonus%" -> "cryo_dmg_",
  )

  override def format(artifact: Artifact): Map[String, Any] = {
    // Capitalizing entire string would leave words like "of" in lowercase
    val setName = artifact.setName.replaceAll("'", "").split(" ").map(_.capitalize).mkString("")
    val slot = artifact.slot.split(" ").head.toLowerCase
    val subStats = artifact.subStats.toList.map {
      case (key, value) => Map("key" -> key, "value" -> value)
    }

    Map(
      "setKey" -> setName,
      "slotKey" -> slot,
      "level" -> artifact.level,
      "rarity" -> artifact.rarity,
      "mainStatKey" -> statNameMap(artifact.mainStat),
      "location" -> "",
      "lock" -> false,
      "substats" -> subStats
    )
  }
}
