
import org.scoverage.coveralls.Imports.CoverallsKeys._

ThisBuild / organization := "kevinlee"
ThisBuild / name := "maven2sbt"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.11.12"

lazy val  hedgehogVersion: String = "64eccc9ca7dbe7a369208a14a97a25d7ccbbda67"

lazy val  hedgehogRepo: Resolver =
    "bintray-scala-hedgehog" at "https://dl.bintray.com/hedgehogqa/scala-hedgehog"

lazy val  hedgehogLibs: Seq[ModuleID] = Seq(
    "qa.hedgehog" %% "hedgehog-core" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-sbt" % hedgehogVersion % Test
  )

lazy val maven2sbt = (project in file("."))
  .settings(
      resolvers += hedgehogRepo
    , libraryDependencies ++= Seq(
        "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
      ) ++ hedgehogLibs
    , testFrameworks := Seq(TestFramework("hedgehog.sbt.Framework"))
    /* Coveralls { */
    , coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        false
      case _ =>
        true
    })
    , coverallsTokenFile := Option(s"""${Path.userHome.absolutePath}/.coveralls-credentials""")
    /* } Coveralls */
  )

