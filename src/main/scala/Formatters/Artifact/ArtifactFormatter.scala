package Formatters.Artifact

import Artifact.Artifact

trait ArtifactFormatter {
  def format(artifact: Artifact): Map[String, Any]
}
