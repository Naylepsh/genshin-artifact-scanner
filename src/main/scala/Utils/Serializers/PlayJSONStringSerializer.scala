package Utils.Serializers

import Formatters.GOODFormat._
import Utils.Serializers.JSONStringSerializer.JSONStringSerializable
import play.api.libs.functional.syntax._
import play.api.libs.json._

object PlayJSONStringSerializer {
  implicit val goodSubStatWrites: OWrites[GOODSubStat] = Json.writes[GOODSubStat]
  implicit val goodSubStatReads: Reads[GOODSubStat] = (
    (JsPath \ "key").read[String] and (JsPath \ "value").read[Double]
    ) (GOODSubStat)
  implicit val goodArtifactWrites: OWrites[GOODArtifact] = Json.writes[GOODArtifact]
  implicit val goodExportWrites: OWrites[GOODExport] = Json.writes[GOODExport]

  implicit object GOODArtifactSerializer extends JSONStringSerializable[GOODArtifact] {
    override def toJSONString(artifact: GOODArtifact): String = Json.toJson(artifact).toString
  }

  implicit object GOODExportSerializer extends JSONStringSerializable[GOODExport] {
    override def toJSONString(export: GOODExport): String = Json.toJson(export).toString
  }
}
