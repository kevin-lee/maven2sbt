
import kevinlee.sbt.SbtCommon.crossVersionProps
import kevinlee.semver.{Major, Minor, SemanticVersion}

val ProjectScalaVersion = "2.13.1"
val CrossScalaVersions = Seq("2.10.7", "2.11.12", "2.12.10", ProjectScalaVersion)

ThisBuild / organization := "kevinlee"
ThisBuild / name := "maven2sbt"
ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := ProjectScalaVersion
ThisBuild / crossScalaVersions := CrossScalaVersions
ThisBuild / developers   := List(
  Developer("Kevin-Lee", "Kevin Lee", "kevin.code@kevinlee.io", url("https://github.com/Kevin-Lee"))
)
ThisBuild / homepage := Some(url("https://github.com/Kevin-Lee/maven2sbt"))
ThisBuild / scmInfo :=
  Some(ScmInfo(
    url("https://github.com/Kevin-Lee/maven2sbt")
    , "https://github.com/Kevin-Lee/maven2sbt.git"
  ))

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
    , libraryDependencies ++=
        crossVersionProps(hedgehogLibs, SemanticVersion.parseUnsafe(scalaVersion.value)) {
          case (Major(2), Minor(10)) =>
            Seq.empty
          case x =>
            Seq("org.scala-lang.modules" %% "scala-xml" % "1.2.0")
        }
    , testFrameworks := Seq(TestFramework("hedgehog.sbt.Framework"))
    /* Coveralls { */
    , coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, 10)) =>
        false
      case _ =>
        true
    })
    /* } Coveralls */
  )

