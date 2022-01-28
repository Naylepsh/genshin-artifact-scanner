package Formatters

import Entities.Artifact
import Entities.Artifact.SetName._
import Entities.Artifact.StatName._
import Entities.Artifact.{SetName, StatName}


object GOODFormat {
  private val statNameMap = Map[StatName.Value, String](
    hpFlat -> "hp",
    hpPercent -> "hp_",
    atkFlat -> "atk",
    atkPercent -> "atk_",
    defFlat -> "def",
    defPercent -> "def_",
    elementalMastery -> "eleMas",
    energyRechargePercent -> "enerRech_",
    healingPercent -> "heal_",
    critRatePercent -> "critRate_",
    critDmgPercent -> "critDMG_",
    physicalDamagePercent -> "physical_dmg_",
    anemoDamagePercent -> "anemo_dmg_",
    geoDamagePercent -> "geo_dmg_",
    electroDamagePercent -> "electro_dmg_",
    hydroDamagePercent -> "hydro_dmg_",
    pyroDamagePercent -> "pyro_dmg_",
    cryoDamagePercent -> "cryo_dmg_",
  )

  private val setNameMap = Map[SetName.Value, String](
    adventurer -> "Adventurer",
    archaicPetra -> "ArchaicPetra",
    berserker -> "Berserker",
    blizzardStrayer -> "BlizzardStrayer",
    bloodstainedChivalry -> "BloodstainedChivalry",
    braveHeart -> "BraveHeart",
    crimsonWitchOfFlames -> "CrimsonWitchOfFlames",
    defendersWill -> "DefendersWill",
    emblemOfSeveredFate -> "EmblemOfSeveredFate",
    gambler -> "Gambler",
    gladiatorsFinale -> "GladiatorsFinale",
    heartOfDepth -> "HeartOfDepth",
    huskOfOpulentDreams -> "HuskOfOpulentDreams",
    instructor -> "Instructor",
    lavawalker -> "Lavawalker",
    luckyDog -> "LuckyDog",
    maidenBeloved -> "MaidenBeloved",
    martialArtist -> "MartialArtist",
    noblesseOblige -> "NoblesseOblige",
    oceanHuedClam -> "OceanHuedClam",
    paleFlame -> "PaleFlame",
    prayersForDestiny -> "PrayersForDestiny",
    prayersForIllumination -> "PrayersForIllumination",
    prayersForWisdom -> "PrayersForWisdom",
    prayersToSpringtime -> "PrayersToSpringtime",
    resolutionOfSojourner -> "ResolutionOfSojourner",
    retracingBolide -> "RetracingBolide",
    scholar -> "Scholar",
    shimenawasReminiscence -> "ShimenawasReminiscence",
    tenacityOfTheMillelith -> "TenacityOfTheMillelith",
    theExile -> "TheExile",
    thunderingFury -> "ThunderingFury",
    thundersoother -> "Thundersoother",
    tinyMiracle -> "TinyMiracle",
    travelingDoctor -> "TravelingDoctor",
    viridescentVenerer -> "ViridescentVenerer",
    wanderersTroupe -> "WanderersTroupe",
  )

  case class GOODSubStat(key: String, value: Double) {}

  case class GOODArtifact(setKey: String, slotKey: String, level: Int, rarity: Int, mainStatKey: String,
                          location: String, lock: Boolean, substats: List[GOODSubStat]) {
  }

  case class GOODExport(format: String = "GOOD", version: Int, source: String, artifacts: List[GOODArtifact])

  object GOODArtifact {
    def apply(artifact: Artifact): GOODArtifact = {
      val setKey = setNameMap(artifact.setName)
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

  object GOODExport {
    def apply(artifacts: List[Artifact]): GOODExport = {
      new GOODExport("GOOD", 1, "", artifacts.map(GOODArtifact.apply))
    }
  }
}
