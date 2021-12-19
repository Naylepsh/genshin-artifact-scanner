package Extract

import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.util.ImageHelper

import java.awt.image.BufferedImage
import scala.util.Try

case class TesseractWrapper(dataPath: String, language: String) {
  val tesseract = new Tesseract()
  tesseract.setDatapath(dataPath)
  tesseract.setLanguage(language)

  def doOCR(image: BufferedImage): Try[String] = Try {
    tesseract.doOCR(image)
  }
}

object TesseractWrapper {
  def cleanupImage(image: BufferedImage): BufferedImage = {
    ImageHelper.convertImageToBinary(image)
  }
}

