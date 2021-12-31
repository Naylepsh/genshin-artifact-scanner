package Extraction


object ArtifactStringExtractor {
  private val subStats = List(
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
    "[a-zA-Z ]+".r.findFirstIn(rawData)

  def extractSubStats(rawData: String): List[(String, Float)] = {
    val subStatLines = rawData.split("\n").filter(_.nonEmpty).map(correctStatLine)
    val flatLines = subStatLines.filter(isFlatStat)
    val percentageLines = subStatLines.filter(isPercentageStat)

    val flatSubStats = subStats.filter(isFlatStat)
    val percentageSubStats = subStats.filter(isPercentageStat)

    val matchedFlats = flatSubStats.flatMap(extractSubStat(flatLines))
    val matchedPercentages = percentageSubStats.flatMap(extractSubStat(percentageLines))
    matchedFlats ++ matchedPercentages
  }

  def extractFirstStatValue(rawData: String): Option[Float] = {
    "[0-9]+.?[0-9]*".r.findFirstIn(rawData)
      .map(_.replaceFirst(",", ""))
      .map(determineDotMeaning)
      .map(_.toFloat)
  }

  private def extractSubStat(subStatLines: Iterable[String])(statName: String): Option[(String, Float)] = {
    val statSubstring = if (isFlatStat(statName)) statName else statName.dropRight(1)
    subStatLines.find(_.contains(statSubstring))
      .map(extractFirstStatValue) match {
      case Some(Some(value)) => Some((statName, value))
      case _ => None
    }
  }

  private def determineDotMeaning(numberString: String): String = {
    /**
     * Artifacts use at most one place after decimal.
     * If there more were found, then the real separator (,) got mistakenly classified as (.),
     * and the following fixes such mistake
     */
    val placesAfterDecimal = numberString.replaceFirst(".+[.]", "").length
    val maxDecimalPlacesUsedByArtifacts = 1
    if (placesAfterDecimal > maxDecimalPlacesUsedByArtifacts)
      numberString.replaceFirst("[.]", "")
    else
      numberString
  }

  private def isPercentageStat(stat: String): Boolean = stat.contains("%")

  private def isFlatStat(stat: String): Boolean = !isPercentageStat(stat)

  private def correctStatLine(statLine: String): String = {
    val Pattern = "(.*)[+](.*)".r
    correctSeparator(statLine) match {
      case Pattern(name, value) => s"$name+${correctStatValue(value)}"
    }
  }

  private def correctStatValue(value: String): String =
    value.replaceAll("[tl]", "1").replaceFirst("H", "11")

  private def correctSeparator(statLine: String): String = {
    if (statLine.contains("+"))
      statLine
    else {
      val statSubstring = subStats.map(stat => if (isFlatStat(stat)) stat else stat.dropRight(1))
        .find(statLine.contains(_)).get
      statLine.replaceFirst(statSubstring + ".", statSubstring + "+")
    }
  }
}
