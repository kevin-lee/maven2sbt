organization := "io.kevinlee"

name := """maven2sbt"""

version := "1.0.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.6",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
