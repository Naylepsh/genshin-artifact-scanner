import Actors.MasterActor
import Actors.MasterActor.Start
import Extraction._
import Scan.ArtifactScanner
import akka.actor.ActorSystem

object Main extends App {
  val dataPath = sys.env("TESSDATA")
  val language = "eng"
  val tesseract = TesseractWrapper(dataPath, language)
  val extractor = ArtifactTesseractExtractor(tesseract)
  val outputDir = sys.env("OUTPUT_DIR")
  val scanner = ArtifactScanner(outputDir)
  val system = ActorSystem("GenshinArtifactScanner")
  val extractors = for (_ <- 1 to 20) yield {
    val tesseract = TesseractWrapper(dataPath, language)
    ArtifactTesseractExtractor(tesseract)
  }
  val master = system.actorOf(MasterActor.props(scanner, extractors.toList))

  master ! Start
}

