name := "genshin-artifact-scanner"

version := "0.1"

scalaVersion := "2.13.7"

libraryDependencies ++= Seq(
  "net.sourceforge.tess4j" % "tess4j" % "4.6.0",
  "org.scalactic" %% "scalactic" % "3.2.10",
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"
)
