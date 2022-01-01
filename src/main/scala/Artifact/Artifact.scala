package Artifact

case class Artifact(
                     setName: String,
                     slot: String,
                     level: Int,
                     mainStat: (String, Float),
                     subStats: Map[String, Float]) {
}
