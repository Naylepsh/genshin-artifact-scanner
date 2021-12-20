case class Artifact(
                     setName: String,
                     pieceType: String,
                     level: Int,
                     mainStat: (String, Int),
                     subStats: Map[String, Int]) {
}

