package Extraction

object ArtifactTesseractCorrector {
  def correctLevel(levelRawData: String): String = {
    levelRawData.replaceFirst("117", "17") // Somehow '+' might have gotten recognized as 1
  }
}
