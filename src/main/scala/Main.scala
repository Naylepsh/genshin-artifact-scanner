import Actors.MasterActor
import Actors.MasterActor.Start
import Extraction._
import Scan.ArtifactScanner
import Utils.Image.Converter
import akka.actor.ActorSystem
import org.apache.commons.io.output.ByteArrayOutputStream

import java.awt.image.BufferedImage
import java.io.File
import java.security.MessageDigest
import javax.imageio.ImageIO

object Main extends App {
  val dataPath = sys.env("TESSDATA")
  val language = "eng"
  val tesseract = TesseractWrapper(dataPath, language)
  val extractor = ArtifactOCRExtractor(tesseract)
  val outputDir = sys.env("OUTPUT_DIR")
  val scanner = ArtifactScanner(outputDir)

  def actorless(): Unit = {
    def manualOCRTest(filename: String, x: Int, y: Int, width: Int, height: Int): Unit = {
      val image = ImageIO.read(new File(filename))
      val subImage = image.getSubimage(x, y, width, height)
      val altered = Converter.monochrome(Converter.invert(subImage))
      val result = tesseract.doOCR(altered)
      println(result)
    }

    //    val filename = "F:/Misc/output/444b7f97-c58a-41ac-beaf-f732e24ea854.png"
    //    manualOCRTest(filename, 20, 150, 210, 30)
    //    manualOCRTest(filename, 20, 180, 45, 40)
    //    println(extractor.extractMainStat(ImageIO.read(new File(filename))))

    val filename = "F:/Misc/output/a1daf6af-cd28-44e5-9af7-ed32d898078f.png"
    manualOCRTest(filename, 0, 0, 55, 25)
    println("Done")
  }


  def withActor(): Unit = {
    val system = ActorSystem("GenshinArtifactScanner")
    val extractors = for (_ <- 1 to 20) yield {
      val tesseract = TesseractWrapper(dataPath, language)
      ArtifactOCRExtractor(tesseract)
    }
    val master = system.actorOf(MasterActor.props(scanner, extractors.toList))

    master ! Start
  }

  def imageHash(): Unit = {
    val path = "F:/Code/genshin-artifact-scanner/src/test/resources/artifacts/5-star-4-stats-goblet.png"
    val image = ImageIO.read(new File(path))
    println(computeImageHash(image))
    val otherImage = ImageIO.read(new File(path))
    println(computeImageHash(otherImage))
    val differentPath = "F:/Code/genshin-artifact-scanner/src/test/resources/artifacts/5-star-4-stats-flower.png"
    val differentImage = ImageIO.read(new File(differentPath))
    println(computeImageHash(differentImage))
  }

  def computeImageHash(image: BufferedImage): String = {
    val data = imageToByteArray(image)
    val hash = hashBytes(data)
    bytesToHexString(hash)
  }

  def hashBytes(bytes: Array[Byte]): Array[Byte] = {
    val md = MessageDigest.getInstance("MD5")
    md.update(bytes)
    val hash = md.digest
    hash
  }

  def imageToByteArray(image: BufferedImage): Array[Byte] = {
    val outputStream = new ByteArrayOutputStream()
    ImageIO.write(image, "png", outputStream)
    outputStream.toByteArray
  }

  def bytesToHexString(bytes: Array[Byte]): String = {
    def byteToHexString(byte: Byte): String = {
      Integer.toString((byte & 0xff) + 0x100, 16).substring(1)
    }

    bytes.map(byteToHexString).mkString("")
  }


  //  actorless()
  withActor()
}

