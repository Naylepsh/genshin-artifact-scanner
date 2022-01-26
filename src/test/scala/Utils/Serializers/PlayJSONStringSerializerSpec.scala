package Utils.Serializers

import Entities.Artifact
import Formatters.GOODFormat.{GOODArtifact, GOODExport, GOODSubStat}
import Utils.Serializers.JSONStringSerializer.JSONStringEnrichment
import Utils.Serializers.PlayJSONStringSerializer._
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json._

class PlayJSONStringSerializerSpec extends AnyFlatSpec with should.Matchers {

  import PlayJSONStringSerializerSpec._

  "Artifact To JSON String" should "preserve data" in {
    val goodArtifact = GOODArtifact(artifact)

    val json = Json.parse(goodArtifact.toJSONString)

    (json \ "setKey").asOpt[String].value shouldBe goodArtifact.setKey
    (json \ "slotKey").asOpt[String].value shouldBe goodArtifact.slotKey
    (json \ "level").asOpt[Int].value shouldBe goodArtifact.level
    (json \ "rarity").asOpt[Int].value shouldBe goodArtifact.rarity
    (json \ "mainStatKey").asOpt[String].value shouldBe goodArtifact.mainStatKey
    (json \ "location").asOpt[String].value shouldBe goodArtifact.location
    (json \ "lock").asOpt[Boolean].value shouldBe goodArtifact.lock
    (json \ "substats").asOpt[List[GOODSubStat]].value.length shouldBe goodArtifact.substats.length
  }

  "Export to JSON String" should "preserve data" in {
    val export = GOODExport(List(artifact, artifact))

    val json = Json.parse(export.toJSONString)

    (json \ "format").asOpt[String].value shouldBe export.format
    (json \ "version").asOpt[Int].value shouldBe export.version
    (json \ "source").asOpt[String].value shouldBe export.source

    /**
     * Testing artifacts preservation is not worth the trouble
     * due to requirement of building custom reads just for the sake of testing
     * because PlayJSON has trouble determining which apply function to use
     */
  }
}

object PlayJSONStringSerializerSpec {
  private val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0f, "CRIT DMG%" -> 17.9f, "CRIT Rate%" -> 3.5f, "HP%" -> 8.2f)).get
}

