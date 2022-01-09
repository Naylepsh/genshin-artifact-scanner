package Scan

trait ArtifactScannable {
  def scanRow(cells: Int, filenames: List[String]): Unit

  def moveRowDown(amount: Int): Unit
}
