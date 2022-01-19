package Utils.Image

import java.awt.Color
import java.awt.image.BufferedImage

object ImageProcessor {
  def monochrome(image: BufferedImage): BufferedImage =
    alter(image)(monochrome)

  private def monochrome(color: Color): Color = {
    val monoThreshold = 300
    val black = new Color(0, 0, 0)
    val white = new Color(255, 255, 255)

    if (color.getRed + color.getGreen + color.getBlue > monoThreshold)
      white
    else
      black
  }

  def invert(image: BufferedImage): BufferedImage =
    alter(image)(invert)

  private def invert(color: Color): Color = {
    new Color(255 - color.getRed,
      255 - color.getGreen,
      255 - color.getBlue)
  }

  private def alter(image: BufferedImage)(alterColor: Color => Color): BufferedImage = {
    val resultingImage = image
    for {
      x <- 0 until resultingImage.getWidth
      y <- 0 until resultingImage.getHeight
    } yield {
      val rgba = resultingImage.getRGB(x, y)
      val color = alterColor(new Color(rgba, true))
      resultingImage.setRGB(x, y, color.getRGB)
    }

    resultingImage
  }
}
