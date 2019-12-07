
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

val ProjectNamePrefix = "maven2sbt"
val ProjectScalaVersion = "2.13.1"
val CrossScalaVersionsWithout2_10 = Seq("2.11.12", "2.12.10", ProjectScalaVersion)
val CrossScalaVersions = "2.10.7" +: CrossScalaVersionsWithout2_10

ThisBuild / organization := "io.kevinlee"
ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := ProjectScalaVersion
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

lazy val core = (project in file("core"))
  .settings(
      name := s"$ProjectNamePrefix-core"
    , crossScalaVersions := CrossScalaVersions
    , resolvers += hedgehogRepo
    , libraryDependencies ++=
      crossVersionProps(hedgehogLibs, SemVer.parseUnsafe(scalaVersion.value)) {
        case (Major(2), Minor(10)) =>
          Seq.empty
        case x =>
          Seq("org.scala-lang.modules" %% "scala-xml" % "1.2.0")
      }
    , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
    /* Coveralls { */
    , coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) =>
          false
        case _ =>
          true
      })
    /* } Coveralls */
  )

lazy val pirateVersion = "9a26abfdcc93b8619ad7b26b33bbd6d7aa302306"
lazy val pirateUri = uri(s"https://github.com/Kevin-Lee/pirate.git#$pirateVersion")

lazy val cli = (project in file("cli"))
    .enablePlugins(JavaAppPackaging)
    .settings(
        name := s"$ProjectNamePrefix-cli"
      , crossScalaVersions := CrossScalaVersionsWithout2_10
      , resolvers += hedgehogRepo
      , libraryDependencies ++= hedgehogLibs
      , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
      /* Coveralls { */
      , coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) =>
          false
        case _ =>
          true
      })
      /* } Coveralls */
    )
    .dependsOn(core, ProjectRef(pirateUri, "pirate"))

lazy val maven2sbt = (project in file("."))
  .settings(
    name := ProjectNamePrefix
  )
  .aggregate(core, cli)

