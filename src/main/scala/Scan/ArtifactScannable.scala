package Scan

import java.awt.image.BufferedImage

trait ArtifactScannable {
  def scanRow(cells: Int): List[BufferedImage]

  def moveRowDown(amount: Int): Unit
}
