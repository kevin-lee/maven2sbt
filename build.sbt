import kevinlee.sbt.SbtCommon.crossVersionProps
import SbtProjectInfo._
import just.semver.SemVer
import SemVer.{Major, Minor}

ThisBuild / organization := "io.kevinlee"
ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}")
  )
)
ThisBuild / homepage := url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}").some
ThisBuild / scmInfo :=
  ScmInfo(
    url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
    s"https://github.com/${props.GitHubUsername}/${props.RepoName}.git"
  ).some
ThisBuild / licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))

lazy val maven2sbt = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin, DocusaurPlugin)
  .settings(
    name := props.RepoName,
    libraryDependencies := paradisePlugin(libraryDependencies.value, SemVer.parseUnsafe(scalaVersion.value)),
    libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    /* GitHub Release { */
    devOopsPackagedArtifacts := List(
      s"cli/target/universal/${name.value}*.zip",
      "cli/target/native-image/maven2sbt-cli-*",
    ),
    /* } GitHub Release */
    /* Website { */
    docusaurDir := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir := docusaurDir.value / "build",
    gitHubPagesOrgName := props.GitHubUsername,
    gitHubPagesRepoName := props.RepoName
    /* } Website */
  )
  .settings(noPublish)
  .aggregate(core, cli)

lazy val core = subProject("core", file("core"))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    crossScalaVersions := props.CrossScalaVersions,
    libraryDependencies ++= {
      val scalaV = scalaVersion.value
      if (scalaV == "3.0.0-RC1")
        List(libs.cats, libs.catsEffect, libs.scalaXmlLatest)
      else if (scalaV.startsWith("3.0"))
        List(libs.catsLatest, libs.catsEffectLatest, libs.scalaXmlLatest)
      else if (scalaV.startsWith("2.11"))
        List(libs.newTypeLib, libs.cats_2_0_0, libs.catsEffect_2_0_0, libs.scalaXml)
      else
        List(libs.newTypeLib, libs.catsLatest, libs.catsEffectLatest, libs.scalaXmlLatest)
    },
    libraryDependencies ++= Seq(
      libs.effectieCatsEffect,
      libs.effectieScalazEffect,
    ),
    libraryDependencies := paradisePlugin(libraryDependencies.value, SemVer.parseUnsafe(scalaVersion.value)),
    libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    wartremoverExcluded += sourceManaged.value,
    /* Build Info { */
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoObject := "Maven2SbtBuildInfo",
    buildInfoPackage := s"${props.RepoName}.info",
    buildInfoOptions += BuildInfoOption.ToJson,
    /* } Build Info */
  )

lazy val pirate = ProjectRef(props.pirateUri, "pirate")

lazy val cli = subProject("cli", file("cli"))
  .enablePlugins(JavaAppPackaging, NativeImagePlugin)
  .settings(
    libraryDependencies := paradisePlugin(libraryDependencies.value, SemVer.parseUnsafe(scalaVersion.value)),
    libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    scalaVersion := (ThisBuild / scalaVersion).value,
    maintainer := "Kevin Lee <kevin.code@kevinlee.io>",
    packageSummary := "Maven2Sbt",
    packageDescription := "A tool to convert Maven pom.xml into sbt build.sbt",
    executableScriptName := props.ExecutableScriptName,
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


// format: off
def prefixedProjectName(name: String) =
  s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// format: on

val removeDottyIncompatible: ModuleID => Boolean =
  m =>
    m.name == "wartremover" ||
      m.name == "ammonite" ||
      m.name == "kind-projector" ||
      m.name == "mdoc" ||
      m.name == "better-monadic-for"

lazy val props =
  new {

    val GitHubUsername       = "Kevin-Lee"
    val RepoName             = "maven2sbt"
    val ExecutableScriptName = RepoName

    val DottyVersion        = "3.0.0-RC3"
    //val ProjectScalaVersion = "2.13.5"
    val ProjectScalaVersion = DottyVersion
    val CrossScalaVersions  =
      List("2.12.13", "2.13.5", ProjectScalaVersion, "3.0.0-RC1", "3.0.0-RC2", DottyVersion).distinct

    val hedgehogVersion       = "0.6.6"
    val hedgehogLatestVersion = "0.6.7"

    val EffectieVersion = "1.10.0"

    lazy val pirateVersion = "main"
    lazy val pirateUri     = uri(s"https://github.com/$GitHubUsername/pirate.git#$pirateVersion")

  }

lazy val libs =
  new {
    def hedgehogLibs(scalaVersion: String): Seq[ModuleID] = {
      val hedgehogV =
        if (scalaVersion == "3.0.0-RC1")
          props.hedgehogVersion
        else
          props.hedgehogLatestVersion
      List(
        "qa.hedgehog" %% "hedgehog-core"   % hedgehogV % Test,
        "qa.hedgehog" %% "hedgehog-runner" % hedgehogV % Test,
        "qa.hedgehog" %% "hedgehog-sbt"    % hedgehogV % Test,
      )
    }

    lazy val scalaXmlLatest = "org.scala-lang.modules" %% "scala-xml" % "2.0.0-RC1"
    lazy val scalaXml       = "org.scala-lang.modules" %% "scala-xml" % "1.3.0"

    lazy val catsLatest: ModuleID       = "org.typelevel" %% "cats-core"   % "2.6.0"
    lazy val cats: ModuleID             = "org.typelevel" %% "cats-core"   % "2.5.0"
    lazy val cats_2_0_0: ModuleID       = "org.typelevel" %% "cats-core"   % "2.0.0"
    lazy val catsEffectLatest: ModuleID = "org.typelevel" %% "cats-effect" % "2.5.0"
    lazy val catsEffect: ModuleID       = "org.typelevel" %% "cats-effect" % "2.4.1"
    lazy val catsEffect_2_0_0: ModuleID = "org.typelevel" %% "cats-effect" % "2.0.0"

    lazy val effectieCatsEffect: ModuleID   = "io.kevinlee" %% "effectie-cats-effect"   % props.EffectieVersion
    lazy val effectieScalazEffect: ModuleID = "io.kevinlee" %% "effectie-scalaz-effect" % props.EffectieVersion

    lazy val newTypeLib: ModuleID = "io.estatico" %% "newtype" % "0.4.4"
  }

def paradisePlugin(
  allLibs: Seq[ModuleID],
  version: SemVer
): Seq[ModuleID] = version match {
  case SemVer(Major(3), _, _, _, _) | SemVer(Major(2), Minor(13), _, _, _) =>
    allLibs.filterNot { x =>
      s"${x.organization}:${x.name}" == "org.scalamacros:paradise"
    }
  case _                                                                   =>
    allLibs
}

def libraryDependenciesPostProcess(
  scalaVersion: String,
  libraries: Seq[ModuleID]
): Seq[ModuleID] =
  if (scalaVersion.startsWith("3.0")) {
    libraries
      .filterNot(removeDottyIncompatible)
  } else {
    libraries
  }

lazy val scala3cLanguageOptions =
  "-language:" + List(
    "dynamics",
    "existentials",
    "higherKinds",
    "reflectiveCalls",
    "experimental.macros",
    "implicitConversions",
  ).mkString(",")

def scalacOptionsPostProcess(scalaVersion: String, options: Seq[String]): Seq[String] =
  if (scalaVersion.startsWith("3.0")) {
    Seq(
      "-source:3.0-migration",
      scala3cLanguageOptions,
      "-Ykind-projector",
      "-siteroot",
      "./dotty-docs",
    )
  } else {
    options
  }

def subProject(projectName: String, path: File): Project =
  Project(projectName, path)
    .settings(
      name := prefixedProjectName(projectName),
      addCompilerPlugin("org.typelevel" % "kind-projector"     % "0.11.3" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy"   %% "better-monadic-for" % "0.3.1"),
      testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework")),
      libraryDependencies ++= libs.hedgehogLibs(scalaVersion.value),
      scalacOptions := scalacOptionsPostProcess(scalaVersion.value, scalacOptions.value).distinct,
      reporterConfig := reporterConfig
        .value
        .withColumnNumbers(true)
        .withSourcePathColor(scala.Console.MAGENTA + scala.Console.UNDERLINED),
      Compile / doc / scalacOptions := ((Compile / doc / scalacOptions)
        .value
        .filterNot(
          if (scalaVersion.value.startsWith("3.0")) {
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
        (console / scalacOptions)
          .value
          .distinct
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / wartremoverErrors := List.empty,
      Test / console / wartremoverWarnings := List.empty,
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .distinct
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
//      , Compile / compile / wartremoverExcluded += sourceManaged.value
//      , Test / compile / wartremoverExcluded += sourceManaged.value
      /* } WartRemover and scalacOptions */
      licenses := List("MIT" -> url("http://opensource.org/licenses/MIT"))
    )
    .settings(
      libraryDependencies ++= (if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("3.0")) {
                                 List.empty[ModuleID]
                               } else {
                                 List(
                                   compilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
                                 )
                               }),
    )
