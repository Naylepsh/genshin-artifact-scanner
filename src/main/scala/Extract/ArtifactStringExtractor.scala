package Extract


object ArtifactStringExtractor {
  def extractLevel(string: String): Option[Int] =
    "[0-9]+".r.findFirstIn(string).map(_.toInt)

  def extractName(rawData: String): Option[String] =
    "[a-zA-Z ]+".r.findFirstIn(rawData)

  def extractStat(rawData: String, statName: String): Option[(String, Float)] = {
    extractStatLine(rawData, statName).map(extractFirstStatValue) match {
      case Some(Some(value)) => Some((statName, value))
      case _ => None
    }
  }

  private def extractStatLine(rawData: String, statName: String): Option[String] = {
    //noinspection ScalaUnnecessaryParentheses,RedundantBlock
    val regex = (s"${statName}.*[^\n]").r
    regex.findFirstIn(rawData)
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
