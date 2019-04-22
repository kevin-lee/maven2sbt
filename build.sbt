
import org.scoverage.coveralls.Imports.CoverallsKeys._

ThisBuild / organization := "kevinlee"
ThisBuild / name := "maven2sbt"
ThisBuild / version := "1.0.0"
ThisBuild / scalaVersion := "2.11.12"

lazy val  hedgehogVersion: String = "55d9828dc6bcdc85ba3ebb31efd541d0a14423bf"

lazy val  hedgehogRepo: Resolver =
  Resolver.url(
    "bintray-scala-hedgehog",
    url("https://dl.bintray.com/hedgehogqa/scala-hedgehog")
  )(Resolver.ivyStylePatterns)

lazy val  hedgehogLibs: Seq[ModuleID] = Seq(
    "hedgehog" %% "hedgehog-core" % hedgehogVersion % Test
  , "hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test
  , "hedgehog" %% "hedgehog-sbt" % hedgehogVersion % Test
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

