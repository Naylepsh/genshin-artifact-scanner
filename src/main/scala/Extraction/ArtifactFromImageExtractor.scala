package Extraction

import Artifact.Artifact

import java.awt.image.BufferedImage
import scala.util.Try

trait ArtifactFromImageExtractor {
  def extractArtifact(image: BufferedImage): Try[Artifact]
}
