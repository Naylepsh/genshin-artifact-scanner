package Extraction

import Entities.Artifact.SetName
import Entities.Artifact.SetName.SetName
import Entities.Artifact.StatName._


object ArtifactStringExtractor {
  val flatSubStats = List(
    atkFlat,
    defFlat,
    elementalMastery,
    hpFlat
  )

  val percentSubStats = List(
    atkPercent,
    critDmgPercent, critRatePercent,
    defPercent,
    energyRechargePercent,
    hpPercent
  )

  def extractInt(string: String): Option[Int] =
    "[0-9]+".r.findFirstIn(string).map(_.toInt)

  def extractSetName(rawData: String): Option[SetName] =
    extractName(rawData).flatMap(SetName.fromString)

  def extractName(rawData: String): Option[String] =
    "[a-zA-Z' -]+".r.findFirstIn(rawData).map(_.trim)

  def extractSubStats(rawData: String): List[(StatName, Double)] = {
    val subStatLines = rawData.split("\n")

    val flatLines = subStatLines.filter(isFlatStat)
    val matchedFlats = flatSubStats.flatMap(extractSubStat(flatLines))

    val percentageLines = subStatLines.filter(isPercentageStat)
    val matchedPercentages = percentSubStats.flatMap(extractSubStat(percentageLines))

    matchedFlats ++ matchedPercentages
  }

  def isFlatStat(stat: String): Boolean = !isPercentageStat(stat)

  private def extractSubStat(subStatLines: Iterable[String])(statName: StatName): Option[(StatName, Double)] = {
    val statSubstring = if (isFlatStat(statName)) statName.toString else statName.toString.dropRight(1)
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

  def isFlatStat(stat: Value): Boolean = !isPercentageStat(stat)

  def isPercentageStat(stat: Value): Boolean = isPercentageStat(stat.toString)

  def isPercentageStat(stat: String): Boolean = stat.contains("%")
}
