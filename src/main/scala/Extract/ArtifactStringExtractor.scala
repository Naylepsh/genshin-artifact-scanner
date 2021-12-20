package Extract

object ArtifactStringExtractor {
  def extractLevel(string: String): Option[Int] =
    "[0-9]+".r.findFirstIn(string).map(_.toInt)

  def extractName(rawData: String): Option[String] =
    "[a-zA-Z ]+".r.findFirstIn(rawData)

  def extractMainStat(rawData: String): Option[(String, Float)] = ???

  def extractStatValue(rawData: String): Option[Float] =
    "[0-9]+.?[0-9]*".r.findFirstIn(rawData)
      .map(_.replaceFirst(",", ""))
      .map(_.toFloat)
}
