package Extraction

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ItemExtractorSpec extends AnyFlatSpec with should.Matchers {

  import Common.Common._
  import ItemExtractor._
  import ItemExtractorSpec._

  "Extract number of items" should "extract the number from dark background" in {
    openImage(pathToItemNumberImage).map(image => {
      extractNumberOfItems(extractor)(image)
    })
  }
}

object ItemExtractorSpec {
  val dataPath: String = sys.env("TESSDATA")
  val language = "eng"
  val tesseract: TesseractWrapper = TesseractWrapper(dataPath, language)
  val extractor: ArtifactOCRExtractor = ArtifactOCRExtractor(tesseract)

  val pathToItemNumberImage = "/item-number.png"
}
