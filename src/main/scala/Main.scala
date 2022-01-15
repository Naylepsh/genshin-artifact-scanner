import Actors.MasterActor
import Actors.MasterActor.Start
import Extraction._
import Scan.ArtifactScanner
import akka.actor.ActorSystem

import java.io.File
import javax.imageio.ImageIO

object Main extends App {
  val dataPath = sys.env("TESSDATA")
  val language = "eng"
  val tesseract = TesseractWrapper(dataPath, language)
  val extractor = ArtifactOCRExtractor(tesseract)
  val outputDir = sys.env("OUTPUT_DIR")
  val scanner = ArtifactScanner(outputDir)
  val system = ActorSystem("GenshinArtifactScanner")

  def actorless(): Unit = {
    val itemsNumberFilename = s"$outputDir/n.png"
    val image = ImageIO.read(new File(itemsNumberFilename))

    scanner.scanItemsNumber(itemsNumberFilename)
    val cells = extractor.extractInt(image)
    //  scrolling down to the last 35 artifacts doesn't work.
    //  Most likely it's only going to be fodder there, so don't bother for now
    val artifactsToSkip = 35

    def manualOCRTest(): Unit = {
      val image = ImageIO.read(new File(".../5-star-4-stats-goblet.png"))
      val subImage = image.getSubimage(30, 310, 50, 30)
      val result = tesseract.doOCR(subImage)
      println(result)
    }

    println("Done")
  }


  def withActor(): Unit = {
    val system = ActorSystem("GenshinArtifactScanner")
    val master = system.actorOf(MasterActor.props(scanner, extractor))

    master ! Start
  }

  withActor()
}
