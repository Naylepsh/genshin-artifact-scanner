package Extraction

import Extraction.ArtifactStringExtractor.{flatSubStats, isFlatStat, percentSubStats}

object ArtifactTesseractCorrector {
  private val subStats = (flatSubStats ++ percentSubStats).map(_.toString)

  def correctLevel(levelRawData: String): String = {
    levelRawData.replaceFirst("117", "17") // Somehow '+' might have gotten recognized as 1
  }

  def correctSetName(setName: String): String = {
    setName
      .replaceAll("’", "'")
      .replaceAll("'+", "'")
      .replaceFirst("Miracte", "Miracle")
      .replaceFirst("sojourner", "Sojourner")
      .trim
  }

  def correctStatName(statName: String): String = {
    /**
     * Fix 'common' typos in OCR. Should handle stat names and set names.
     * TODO: Use some better approach that hardcoded regexes. Something like a word-similarity confidence score?
     */
    statName
      .replaceFirst("Hydr.", "Hydro")
      .replaceFirst("Anemeo", "Anemo")
  }

  def correctSubStats(rawData: String): String =
    rawData
      .split("\n")
      .filter(_.nonEmpty)
      .map(data => correctFirstStatValue(correctStatLine(data)))
      .mkString("\n")

  private def correctStatLine(statLine: String): String = {
    val Pattern = "(.*)[+](.*)".r
    correctSeparator(statLine) match {
      case Pattern(name, value) => s"$name+${correctNumericValue(value)}"
    }
  }

  def correctNumericValue(value: String): String = {
    /**
     * Sometimes values that should had been fully numeric
     * end up having some of their characters mistakenly identified as letters
     */
    value
      .replaceAll("[tl]", "1")
      .replaceAll("[a]", "4")
      .replaceFirst("HI", "11")
      .replaceFirst("[HN]", "11")
      .replaceAll("[I]", "1")
      .replaceAll("[sS]", "5")
  }

  private def correctSeparator(statLine: String): String = {
    /**
     * Sub stat name and value are separated by '+', like: HP+123
     * However, sometimes that '+' gets mistakenly identified as 't' (or possibly some other chars)
     */
    if (statLine.contains("+"))
      statLine
    else {
      val statSubstring = subStats.map(stat => if (isFlatStat(stat)) stat else stat.dropRight(1))
        .find(statLine.contains(_)).get
      statLine.replaceFirst(statSubstring + ".", statSubstring + "+")
    }
  }

  private def correctFirstStatValue(rawData: String): String = {
    val valuePattern = "[0-9]+.?[0-9]*".r
    val correctedSubstitute = valuePattern.findFirstIn(rawData)
      .map(_.replaceFirst(",", ""))
      .map(determineDotMeaning)
    correctedSubstitute.map(valuePattern.replaceFirstIn(rawData, _)).getOrElse(rawData)
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
