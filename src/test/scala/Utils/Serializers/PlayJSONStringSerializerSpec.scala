package Utils.Serializers

import Artifact.Artifact
import Formatters.GOODFormat.GOODArtifact
import Utils.Serializers.JSONStringSerializer.JSONStringEnrichment
import Utils.Serializers.PlayJSONStringSerializer.GOODArtifactSerializer
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json._

class PlayJSONStringSerializerSpec extends AnyFlatSpec with should.Matchers {


  import PlayJSONStringSerializerSpec._

  "To JSON String" should "preserve data" in {
    val goodArtifact = GOODArtifact(artifact)

    val json = Json.parse(goodArtifact.toJSONString)

    (json \ "setKey").asOpt[String].value shouldBe goodArtifact.setKey
    (json \ "slotKey").asOpt[String].value shouldBe goodArtifact.slotKey
    (json \ "level").asOpt[Int].value shouldBe goodArtifact.level
    (json \ "rarity").asOpt[Int].value shouldBe goodArtifact.rarity
    (json \ "mainStatKey").asOpt[String].value shouldBe goodArtifact.mainStatKey
    (json \ "location").asOpt[String].value shouldBe goodArtifact.location
    (json \ "lock").asOpt[Boolean].value shouldBe goodArtifact.lock
  }
}

object PlayJSONStringSerializerSpec {
  private val artifact = Artifact(
    setName = "Husk of Opulent Dreams", slot = "Flower of Life", level = 20, rarity = 5, mainStat = "HP",
    subStats = Map("Energy Recharge%" -> 11.0f, "CRIT DMG%" -> 17.9f, "CRIT Rate%" -> 3.5f, "HP%" -> 8.2f)).get
}
