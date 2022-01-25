package Utils.Converters

import Artifact.Artifact
import Formatters.GOODFormat._
import play.api.libs.json._

object PlayJSONStringConverter extends App {
  val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0, "CRIT DMG%" -> 17.9, "CRIT Rate%" -> 3.5, "HP%" -> 8.2)).get
  val goodArtifact = GOODArtifact(artifact)
  implicit val goodSubStatFormat = Json.writes[GOODSubStat]
  implicit val goodArtifactFormat = Json.writes[GOODArtifact]

  val json = Json.toJson(goodArtifact)
  println(json.toString())
}
