
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

val ProjectNamePrefix = "maven2sbt"
val ProjectVersion = "0.4.0"
val ProjectScalaVersion = "2.13.1"
val CrossScalaVersions = Seq("2.11.12", "2.12.10", ProjectScalaVersion)

ThisBuild / organization := "io.kevinlee"
ThisBuild / version := ProjectVersion
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

lazy val hedgehogLibs: Seq[ModuleID] = Seq(
    "qa.hedgehog" %% "hedgehog-core" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-sbt" % hedgehogVersion % Test
  )

lazy val cats: ModuleID = "org.typelevel" %% "cats-core" % "2.1.0"
lazy val cats_2_0_0: ModuleID = "org.typelevel" %% "cats-core" % "2.0.0"
lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % "2.0.0"

lazy val core = (project in file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
      name := s"$ProjectNamePrefix-core"
    , crossScalaVersions := CrossScalaVersions
    , addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    , resolvers += hedgehogRepo
    , libraryDependencies ++= crossVersionProps(
        hedgehogLibs ++ Seq("org.scala-lang.modules" %% "scala-xml" % "1.2.0", catsEffect)
      , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(11)) =>
            Seq(cats_2_0_0)
          case _ =>
            Seq(cats)
        }
    , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
    /* Build Info { */
    , buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)
    , buildInfoObject := "Maven2SbtBuildInfo"
    , buildInfoPackage := "maven2sbt.info"
    , buildInfoOptions += BuildInfoOption.ToJson
    /* } Build Info */
    /* publish { */
    , licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
    /* } publish */

  )

lazy val pirateVersion = "44486bc961b52ba889f0b8f2b23f719d0ed8ba99"
lazy val pirateUri = uri(s"https://github.com/Kevin-Lee/pirate.git#$pirateVersion")

lazy val cli = (project in file("cli"))
  .enablePlugins(JavaAppPackaging)
  .settings(
      name := s"$ProjectNamePrefix-cli"
    , resolvers += hedgehogRepo
    , libraryDependencies ++= hedgehogLibs
    , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
    , maintainer := "Kevin Lee <kevin.code@kevinlee.io>"
    , packageSummary := "Maven2Sbt"
    , packageDescription := "A tool to convert Maven pom.xml into sbt build.sbt"
    , executableScriptName := ProjectNamePrefix
  )
  .dependsOn(core, ProjectRef(pirateUri, "pirate"))

lazy val maven2sbt = (project in file("."))
  .enablePlugins(DevOopsGitReleasePlugin)
  .settings(
      name := ProjectNamePrefix
      /* GitHub Release { */
    , devOopsPackagedArtifacts := List(
        s"core/target/scala-*/${name.value}*.jar"
      , s"cli/target/universal/${name.value}*.zip"
      , s"cli/target/universal/${name.value}*.tgz"
      , s"cli/target/${name.value}*.deb"
      )
    /* } GitHub Release */
  )
  .aggregate(core, cli)

