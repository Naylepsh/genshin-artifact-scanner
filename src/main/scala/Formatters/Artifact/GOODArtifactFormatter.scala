package Formatters.Artifact

import Artifact.Artifact

object GOODArtifactFormatter extends ArtifactFormatter {
  override def format(artifact: Artifact): Map[String, Any] = {
    // Capitalizing entire string would leave words like "of" in lowercase
    val setName = artifact.setName.replaceAll("'", "").split(" ").map(_.capitalize).mkString("")
    val slot = artifact.slot.split(" ").head.toLowerCase

    Map(
      "setKey" -> setName,
      "slotKey" -> slot,
      "level" -> artifact.level,
      "rarity" -> artifact.rarity,
      "location" -> "",
      "lock" -> false,
    )
  }
}
