package Artifact

// TODO: calculate main stat value based on mainStat, level and rarity
case class Artifact(
                     setName: String,
                     slot: String,
                     level: Int,
                     rarity: Int,
                     mainStat: String,
                     subStats: Map[String, Float]) {
}
