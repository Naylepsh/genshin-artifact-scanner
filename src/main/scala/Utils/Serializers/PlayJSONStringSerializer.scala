package Utils.Serializers

import Formatters.GOODFormat._
import Utils.Serializers.JSONStringSerializer.JSONStringSerializable
import play.api.libs.json._

object PlayJSONStringSerializer {
  implicit val goodSubStatFormat: OWrites[GOODSubStat] = Json.writes[GOODSubStat]
  implicit val goodArtifactFormat: OWrites[GOODArtifact] = Json.writes[GOODArtifact]

  implicit object GOODArtifactSerializer extends JSONStringSerializable[GOODArtifact] {
    override def toJSONString(artifact: GOODArtifact): String = Json.toJson(artifact).toString
  }
}
