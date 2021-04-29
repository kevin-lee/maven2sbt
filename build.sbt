import kevinlee.sbt.SbtCommon.crossVersionProps
import SbtProjectInfo._
import just.semver.SemVer
import SemVer.{Major, Minor}

val GitHubUsername = "Kevin-Lee"
val RepoName = "maven2sbt"
val ExecutableScriptName = RepoName

val DottyVersion = "3.0.0-RC3"
//val ProjectScalaVersion = "2.13.5"
val ProjectScalaVersion = DottyVersion
val CrossScalaVersions = Seq("2.12.12", "2.13.5", ProjectScalaVersion, "3.0.0-RC1", "3.0.0-RC2", DottyVersion).distinct

ThisBuild / organization := "io.kevinlee"
ThisBuild / version := ProjectVersion
ThisBuild / scalaVersion := ProjectScalaVersion
ThisBuild / developers   := List(
  Developer(GitHubUsername, "Kevin Lee", "kevin.code@kevinlee.io", url(s"https://github.com/$GitHubUsername"))
)
ThisBuild / homepage := url(s"https://github.com/$GitHubUsername/$RepoName").some
ThisBuild / scmInfo :=
  ScmInfo(
    url(s"https://github.com/$GitHubUsername/$RepoName"),
    s"https://github.com/$GitHubUsername/$RepoName.git"
  ).some
ThisBuild / licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))

def prefixedProjectName(name: String) = s"$RepoName${if (name.isEmpty) "" else s"-$name"}"

val removeDottyIncompatible: ModuleID => Boolean =
  m =>
    m.name == "wartremover" ||
      m.name == "ammonite" ||
      m.name == "kind-projector" ||
      m.name == "mdoc" ||
      m.name == "better-monadic-for"

lazy val hedgehogVersion: String = "0.6.6"
lazy val hedgehogLatestVersion: String = "0.6.7"

def hedgehogLibs(scalaVersion: String): Seq[ModuleID] = {
  val hedgehogV = if (scalaVersion == "3.0.0-RC1") hedgehogVersion else hedgehogLatestVersion
  List(
    "qa.hedgehog" %% "hedgehog-core"   % hedgehogV % Test,
    "qa.hedgehog" %% "hedgehog-runner" % hedgehogV % Test,
    "qa.hedgehog" %% "hedgehog-sbt"    % hedgehogV % Test,
  )
}

lazy val scalaXmlLatest = "org.scala-lang.modules" %% "scala-xml" % "2.0.0-RC1"
lazy val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.3.0"

lazy val catsLatest: ModuleID = "org.typelevel" %% "cats-core" % "2.6.0"
lazy val cats: ModuleID = "org.typelevel" %% "cats-core" % "2.5.0"
lazy val cats_2_0_0: ModuleID = "org.typelevel" %% "cats-core" % "2.0.0"
lazy val catsEffectLatest: ModuleID = "org.typelevel" %% "cats-effect" % "2.5.0"
lazy val catsEffect: ModuleID = "org.typelevel" %% "cats-effect" % "2.4.1"
lazy val catsEffect_2_0_0: ModuleID = "org.typelevel" %% "cats-effect" % "2.0.0"

val EffectieVersion = "1.10.0"
lazy val effectieCatsEffect: ModuleID = "io.kevinlee" %% "effectie-cats-effect" % EffectieVersion
lazy val effectieScalazEffect: ModuleID = "io.kevinlee" %% "effectie-scalaz-effect" % EffectieVersion

lazy val newTypeLib: ModuleID = "io.estatico" %% "newtype" % "0.4.4"

def paradisePlugin(
  allLibs: Seq[ModuleID],
  version: SemVer
): Seq[ModuleID] = version match {
  case SemVer(Major(3), _, _, _, _) | SemVer(Major(2), Minor(13), _, _, _) =>
    allLibs.filterNot { x =>
      s"${x.organization}:${x.name}" == "org.scalamacros:paradise"
    }
  case _ =>
    allLibs
}

def libraryDependenciesPostProcess(
  isDotty: Boolean,
  libraries: Seq[ModuleID]
): Seq[ModuleID] = (
  if (isDotty) {
    libraries
      .filterNot(removeDottyIncompatible)
  } else
    libraries
  )

lazy val scala3cLanguageOptions = "-language:" + List(
  "dynamics",
  "existentials",
  "higherKinds",
  "reflectiveCalls",
  "experimental.macros",
  "implicitConversions",
).mkString(",")

def scalacOptionsPostProcess(isDotty: Boolean, options: Seq[String]): Seq[String] =
  if (isDotty) {
    Seq(
      "-source:3.0-migration",
      scala3cLanguageOptions,
      "-Ykind-projector",
      "-siteroot", "./dotty-docs",
    )
  } else {
    options
  }

def subProject(projectName: String, path: File): Project =
  Project(projectName, path)
    .settings(
      name := prefixedProjectName(projectName),
      addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework")),
      libraryDependencies ++= hedgehogLibs(scalaVersion.value),
      scalacOptions := scalacOptionsPostProcess(isDotty.value, scalacOptions.value).distinct,
      reporterConfig := reporterConfig.value
        .withColumnNumbers(true)
        .withSourcePathColor(scala.Console.MAGENTA + scala.Console.UNDERLINED),
      Compile / doc / scalacOptions := ((Compile / doc / scalacOptions).value.filterNot(
        if (isDotty.value) {
          Set(
            "-source:3.0-migration",
            "-scalajs",
            "-deprecation",
            "-explain-types",
            "-explain",
            "-feature",
            scala3cLanguageOptions,
            "-unchecked",
            "-Xfatal-warnings",
            "-Ykind-projector",
            "-from-tasty",
            "-encoding",
            "utf8",
          )
        } else {
          Set.empty[String]
        }
      )),
      Compile / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src/main"
        if (scalaVersion.value.startsWith("3.0"))
          Seq(
            sharedSourceDir / "scala-3.0",
          )
        else if (scalaVersion.value.startsWith("2."))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
          )
        else
          Seq.empty
      },
      /* WartRemover and scalacOptions { */
//      , Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value)
//      , Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value)
      wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
//            , wartremoverErrors ++= Warts.all
      Compile / console / wartremoverErrors := List.empty,
      Compile / console / wartremoverWarnings := List.empty,
      Compile / console / scalacOptions :=
        (console / scalacOptions).value
          .distinct
          .filterNot(option =>option.contains("wartremover") || option.contains("import")),
      Test / console / wartremoverErrors := List.empty,
      Test / console / wartremoverWarnings := List.empty,
      Test / console / scalacOptions :=
        (console / scalacOptions).value
          .distinct
          .filterNot( option => option.contains("wartremover") || option.contains("import")),
//      , Compile / compile / wartremoverExcluded += sourceManaged.value
//      , Test / compile / wartremoverExcluded += sourceManaged.value
      /* } WartRemover and scalacOptions */
      licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))
    )
    .settings(
      libraryDependencies ++= (
        if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("3.0")) {
          List.empty[ModuleID]
        } else {
          List(
            compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
          )
        }),
    )

lazy val core = subProject("core", file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    crossScalaVersions := CrossScalaVersions,
    libraryDependencies ++= {
      val scalaV = scalaVersion.value
      if (scalaV == "3.0.0-RC1")
        List(cats, catsEffect, scalaXmlLatest)
      else if (scalaV.startsWith("3.0"))
        List(catsLatest, catsEffectLatest, scalaXmlLatest)
      else if (scalaV.startsWith("2.11"))
        List(newTypeLib, cats_2_0_0, catsEffect_2_0_0, scalaXml)
      else
        List(newTypeLib, catsLatest, catsEffectLatest, scalaXmlLatest)
    },
    libraryDependencies ++= Seq(
      effectieCatsEffect,
      effectieScalazEffect,
    ),
    libraryDependencies := paradisePlugin(libraryDependencies.value, SemVer.parseUnsafe(scalaVersion.value)),
    libraryDependencies := libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    wartremoverExcluded += sourceManaged.value,
    /* Build Info { */
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoObject := "Maven2SbtBuildInfo",
    buildInfoPackage := s"$RepoName.info",
    buildInfoOptions += BuildInfoOption.ToJson,
    /* } Build Info */
  )

lazy val pirateVersion = "main"
lazy val pirateUri = uri(s"https://github.com/$GitHubUsername/pirate.git#$pirateVersion")
lazy val pirate = ProjectRef(pirateUri, "pirate")

lazy val cli = subProject("cli", file("cli"))
  .enablePlugins(JavaAppPackaging, NativeImagePlugin)
  .settings(
    libraryDependencies := paradisePlugin(libraryDependencies.value, SemVer.parseUnsafe(scalaVersion.value)),
    libraryDependencies := libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    scalaVersion := (ThisBuild / scalaVersion).value,
    maintainer := "Kevin Lee <kevin.code@kevinlee.io>",
    packageSummary := "Maven2Sbt",
    packageDescription := "A tool to convert Maven pom.xml into sbt build.sbt",
    executableScriptName := ExecutableScriptName,
//    nativeImageVersion := "21.1.0",
//    nativeImageJvm := "graalvm-java11",
    nativeImageOptions ++= Seq(
      "-H:+ReportExceptionStackTraces",
      "--initialize-at-build-time",
      "--verbose",
      "--no-fallback",
//      "--report-unsupported-elements-at-runtime",
//      "--allow-incomplete-classpath",
//      "--initialize-at-build-time=scala.runtime.Statics",
//      "--initialize-at-build-time=scala.Enumeration.populateNameMap",
//      "--initialize-at-build-time=scala.Enumeration.getFields$1",
    ),
  )
  .settings(noPublish)
  .dependsOn(core, pirate)


lazy val maven2sbt = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin, DocusaurPlugin)
  .settings(
    name := RepoName,
    libraryDependencies := paradisePlugin(libraryDependencies.value, SemVer.parseUnsafe(scalaVersion.value)),
    libraryDependencies := libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    /* GitHub Release { */
    devOopsPackagedArtifacts := List(
      s"cli/target/universal/${name.value}*.zip",
      "cli/target/native-image/maven2sbt-cli-*",
    ),
    /* } GitHub Release */
    /* Website { */
    docusaurDir := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir := docusaurDir.value / "build",

    gitHubPagesOrgName := GitHubUsername,
    gitHubPagesRepoName := RepoName
    /* } Website */
  )
  .settings(noPublish)
  .aggregate(core, cli)

