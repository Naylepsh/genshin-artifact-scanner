package Extraction

import java.awt.image.BufferedImage
import scala.util.Try

trait NumberExtractable {
  def extractInt(image: BufferedImage): Try[Int]
}
