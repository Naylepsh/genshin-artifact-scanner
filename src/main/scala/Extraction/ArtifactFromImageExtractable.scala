package Extraction

import Artifact.Artifact

import java.awt.image.BufferedImage
import scala.util.Try

trait ArtifactFromImageExtractable {
  def extractArtifact(image: BufferedImage): Try[Artifact]
}
