package Extraction


object ArtifactStringExtractor {
  val subStats = List(
    "ATK", "ATK%",
    "CRIT DMG%", "CRIT Rate%",
    "DEF", "DEF%",
    "Elemental Mastery",
    "Energy Recharge%",
    "HP", "HP%"
  )

  def extractInt(string: String): Option[Int] =
    "[0-9]+".r.findFirstIn(string).map(_.toInt)

  def extractName(rawData: String): Option[String] =
    "[a-zA-Z ]+".r.findFirstIn(rawData).map(_.trim)

  def extractSubStats(rawData: String): List[(String, Double)] = {
    val subStatLines = rawData.split("\n")
    val flatLines = subStatLines.filter(isFlatStat)
    val percentageLines = subStatLines.filter(isPercentageStat)

    val flatSubStats = subStats.filter(isFlatStat)
    val percentageSubStats = subStats.filter(isPercentageStat)

    val matchedFlats = flatSubStats.flatMap(extractSubStat(flatLines))
    val matchedPercentages = percentageSubStats.flatMap(extractSubStat(percentageLines))
    matchedFlats ++ matchedPercentages
  }

  private def extractSubStat(subStatLines: Iterable[String])(statName: String): Option[(String, Double)] = {
    val statSubstring = if (isFlatStat(statName)) statName else statName.dropRight(1)
    subStatLines.find(_.contains(statSubstring))
      .map(extractFirstStatValue) match {
      case Some(Some(value)) => Some((statName, value))
      case _ => None
    }
  }

  def extractFirstStatValue(rawData: String): Option[Float] = {
    "[0-9]+.?[0-9]*".r.findFirstIn(rawData)
      .map(_.replaceFirst(",", ""))
      .map(_.toFloat)
  }

  def isFlatStat(stat: String): Boolean = !isPercentageStat(stat)

  def isPercentageStat(stat: String): Boolean = stat.contains("%")
}
