package Extraction

import Entities.Artifact

import java.awt.image.BufferedImage
import scala.util.Try

trait ArtifactFromImageExtractable {
  def extractArtifact(image: BufferedImage): Try[Artifact]
}
