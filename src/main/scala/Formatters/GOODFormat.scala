package Formatters

import Artifact.Artifact


object GOODFormat {
  private val statNameMap = Map[String, String](
    "HP" -> "hp",
    "HP%" -> "hp_",
    "ATK" -> "atk",
    "ATK%" -> "atk_",
    "DEF" -> "def",
    "DEF%" -> "def_",
    "Elemental Mastery" -> "eleMas",
    "Energy Recharge%" -> "enerRech_",
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

  case class GOODSubStat(key: String, value: Double) {}

  case class GOODArtifact(setKey: String, slotKey: String, level: Int, rarity: Int, mainStatKey: String,
                          location: String, lock: Boolean, substats: List[GOODSubStat]) {
  }

  object GOODArtifact {
    def apply(artifact: Artifact): GOODArtifact = {
      val setKey = artifact.setName.replaceAll("'", "").split(" ").map(_.capitalize).mkString("")
      val slotKey = artifact.slot.split(" ").head.toLowerCase
      val level = artifact.level
      val rarity = artifact.rarity
      val mainStatKey: String = statNameMap(artifact.mainStat)
      val location = ""
      val lock = false
      val subStats = artifact.subStats.toList.map {
        case (key, value) => GOODSubStat(statNameMap(key), value)
      }

      new GOODArtifact(setKey, slotKey, level, rarity, mainStatKey, location, lock, subStats)
    }
  }
}
