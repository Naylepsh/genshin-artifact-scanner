package Extraction

import Utils.Image.Converter._

import java.awt.image.BufferedImage
import scala.util.Try

object ItemExtractor {
  def extractNumberOfItems(extractor: NumberExtractable)(image: BufferedImage): Try[Int] = {
    /**
     * Item number is a white font on a dark background.
     * OCR seems to have issues with detecting anything meaningful in such cases, thus this conversion.
     */
    extractor.extractInt(monochrome(invert(image)))
  }
}
