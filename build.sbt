
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

val ProjectNamePrefix = "maven2sbt"
val ProjectVersion = "0.4.0"
val ProjectScalaVersion = "2.13.3"
val CrossScalaVersions = Seq("2.11.12", "2.12.12", ProjectScalaVersion)

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

lazy val cats: ModuleID = "org.typelevel" %% "cats-core" % "2.1.1"
lazy val cats_2_0_0: ModuleID = "org.typelevel" %% "cats-core" % "2.0.0"
lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % "2.1.4"
lazy val catsEffect_2_0_0: ModuleID = "org.typelevel" %% "cats-effect" % "2.0.0"

val EffectieVersion = "1.0.0"
lazy val effectieCatsEffect: ModuleID = "io.kevinlee" %% "effectie-cats-effect" % EffectieVersion
lazy val effectieScalazEffect: ModuleID = "io.kevinlee" %% "effectie-scalaz-effect" % EffectieVersion

def subProject(projectName: String, path: File): Project =
  Project(projectName, path)
    .settings(
        name := s"$ProjectNamePrefix-$projectName"
      , addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
      , resolvers += hedgehogRepo
      , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
      , libraryDependencies ++= hedgehogLibs
      , scalacOptions := (SemVer.parseUnsafe(scalaVersion.value) match {
          case SemVer(Major(2), Minor(13), _, _, _) =>
            scalacOptions.value.filter(_ != "-Xlint:nullary-override") ++ Seq("-Wconf:cat=lint-byname-implicit:s")// ++ Seq("-Xlint:-multiarg-infix")
          case _ =>
            scalacOptions.value
        })
    )

lazy val core = subProject("core", file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
      crossScalaVersions := CrossScalaVersions
    , libraryDependencies ++= crossVersionProps(
        Seq("org.scala-lang.modules" %% "scala-xml" % "1.2.0", effectieCatsEffect, effectieScalazEffect)
      , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(11)) =>
            Seq(cats_2_0_0, catsEffect_2_0_0)
          case _ =>
            Seq(cats, catsEffect)
        }
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

lazy val pirateVersion = "b3a0a3eff3a527dff542133aaf0fd935aa2940fc"
lazy val pirateUri = uri(s"https://github.com/Kevin-Lee/pirate.git#$pirateVersion")

lazy val cli = subProject("cli", file("cli"))
  .enablePlugins(JavaAppPackaging)
  .settings(
      maintainer := "Kevin Lee <kevin.code@kevinlee.io>"
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

