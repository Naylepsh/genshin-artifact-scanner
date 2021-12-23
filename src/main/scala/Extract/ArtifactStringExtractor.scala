package Extract


object ArtifactStringExtractor {
  private val subStats = List(
    "ATK", "ATK%",
    "CRIT DMG", "CRIT RATE",
    "DEF", "DEF%",
    "Elemental Mastery",
    "Energy Recharge",
    "HP", "HP%")

  def extractLevel(string: String): Option[Int] =
    "[0-9]+".r.findFirstIn(string).map(_.toInt)

  def extractName(rawData: String): Option[String] =
    "[a-zA-Z ]+".r.findFirstIn(rawData)

  def extractSubStats(rawData: String): List[(String, Float)] =
    subStats.flatMap(extractSubStat(rawData, _))

  def extractSubStat(rawData: String, statName: String): Option[(String, Float)] = {
    def isPercentageType(artifactLine: String): Boolean = artifactLine.contains("%")

    val matchesArtifactType = if (isPercentageType(statName))
      isPercentageType _
    else
      (artifactLine: String) => !isPercentageType(artifactLine)

    rawData.split("\n")
      .filter(matchesArtifactType)
      .find(_.contains(statName.dropRight(1)))
      .map(extractFirstStatValue) match {
      case Some(Some(value)) => Some((statName, value))
      case _ => None
    }
  }

  def extractFirstStatValue(rawData: String): Option[Float] = {
    "[0-9]+.?[0-9]*".r.findFirstIn(rawData)
      .map(_.replaceFirst(",", ""))
      .map(determineDotMeaning)
      .map(_.toFloat)
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
}
