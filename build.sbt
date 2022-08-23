import SbtProjectInfo._

ThisBuild / organization               := "io.kevinlee"
ThisBuild / scalaVersion               := props.ProjectScalaVersion
ThisBuild / developers                 := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}")
  )
)
ThisBuild / homepage                   := url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}").some
ThisBuild / scmInfo                    :=
  ScmInfo(
    url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
    s"https://github.com/${props.GitHubUsername}/${props.RepoName}.git"
  ).some
ThisBuild / licenses                   := List("MIT" -> url("http://opensource.org/licenses/MIT"))
ThisBuild / useAggressiveScalacOptions := true

lazy val maven2sbt = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin, DocusaurPlugin)
  .settings(
    name                     := props.RepoName,
    libraryDependencies      := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    /* GitHub Release { */
    devOopsPackagedArtifacts := List(
      s"modules/${props.RepoName}-cli/target/universal/${name.value}*.zip",
      s"modules/${props.RepoName}-cli/target/native-image/maven2sbt-cli-*",
    ),
    /* } GitHub Release */
    /* Website { */
    docusaurDir              := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir         := docusaurDir.value / "build",
    /* } Website */
  )
  .settings(noPublish)
  .aggregate(core, cli)

lazy val core = subProject("core")
  .enablePlugins(BuildInfoPlugin)
  .settings(
//    resolvers += Resolver.sonatypeRepo("snapshots"),
    crossScalaVersions  := props.CrossScalaVersions,
    libraryDependencies ++= {
      val scalaV = scalaVersion.value
      if (scalaV.startsWith("3"))
        List(libs.catsLib, libs.scalaXmlLatest)
      else if (scalaV.startsWith("2.11"))
        List(libs.newTypeLib, libs.cats_2_0_0, libs.scalaXml)
      else
        List(libs.newTypeLib, libs.catsLib, libs.scalaXmlLatest)
    },
    libraryDependencies ++= Seq(
      libs.effectieCatsEffect,
    ) ++ List(libs.extrasCats),
    libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    wartremoverExcluded ++= List(sourceManaged.value),
    /* Build Info { */
    buildInfoKeys       := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoObject     := "Maven2SbtBuildInfo",
    buildInfoPackage    := s"${props.RepoName}.info",
    buildInfoOptions += BuildInfoOption.ToJson,
    /* } Build Info */
  )

lazy val pirate = ProjectRef(props.pirateUri, "pirate-scalaz")

lazy val cli = subProject("cli")
  .enablePlugins(JavaAppPackaging, NativeImagePlugin)
  .settings(
    libraryDependencies  := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    scalaVersion         := (ThisBuild / scalaVersion).value,
    maintainer           := "Kevin Lee <kevin.code@kevinlee.io>",
    packageSummary       := "Maven2Sbt",
    packageDescription   := "A tool to convert Maven pom.xml into sbt build.sbt",
    executableScriptName := props.ExecutableScriptName,
    nativeImageVersion := "22.2.0",
    nativeImageJvm := "graalvm-java17",
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
//    m.name == "wartremover" ||
      m.name == "ammonite" ||
      m.name == "kind-projector" ||
      m.name == "mdoc" ||
      m.name == "better-monadic-for"

lazy val props =
  new {

    final val GitHubUsername       = "Kevin-Lee"
    final val RepoName             = "maven2sbt"
    final val ExecutableScriptName = RepoName

    final val DottyVersion        = "3.1.2"
//    final val ProjectScalaVersion = "2.13.5"
    final val ProjectScalaVersion = DottyVersion
    final val CrossScalaVersions  = List("2.12.14", "2.13.6", ProjectScalaVersion, DottyVersion).distinct

    final val hedgehogVersion = "0.8.0"

//    final val canEqualVersion = "0.1.0"

    final val EffectieVersion = "1.16.0"

    final val pirateVersion = "4e8177ec1548780cbf62b0352e58bceb7a99bfd6"
    final val pirateUri     = uri(s"https://github.com/$GitHubUsername/pirate.git#$pirateVersion")

    final val scalaXml1 = "1.3.0"
    final val scalaXml2 = "2.1.0"

    final val ExtrasVersion = "0.13.0"

  }

lazy val libs =
  new {
    lazy val hedgehogLibs: List[ModuleID] = List(
      "qa.hedgehog" %% "hedgehog-core"   % props.hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-runner" % props.hedgehogVersion % Test,
      "qa.hedgehog" %% "hedgehog-sbt"    % props.hedgehogVersion % Test,
    )

//    lazy val canEqual = "io.kevinlee" %% "can-equal" % props.canEqualVersion

    lazy val scalaXmlLatest = "org.scala-lang.modules" %% "scala-xml" % props.scalaXml2
    lazy val scalaXml       = "org.scala-lang.modules" %% "scala-xml" % props.scalaXml1

    lazy val catsLib    = "org.typelevel" %% "cats-core" % "2.7.0"
    lazy val cats_2_0_0 = "org.typelevel" %% "cats-core" % "2.0.0"

    lazy val catsEffectLib    = "org.typelevel" %% "cats-effect" % "2.5.4"
    lazy val catsEffect_2_0_0 = "org.typelevel" %% "cats-effect" % "2.0.0"

    lazy val effectieCatsEffect   = "io.kevinlee" %% "effectie-cats-effect"   % props.EffectieVersion
    lazy val effectieScalazEffect = "io.kevinlee" %% "effectie-scalaz-effect" % props.EffectieVersion

    lazy val newTypeLib = "io.estatico" %% "newtype" % "0.4.4"

    lazy val extrasCats = "io.kevinlee" %% "extras-cats" % props.ExtrasVersion
  }

def libraryDependenciesPostProcess(
  scalaVersion: String,
  libraries: Seq[ModuleID]
): Seq[ModuleID] =
  if (scalaVersion.startsWith("3.")) {
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
  if (scalaVersion.startsWith("3.")) {
    Seq(
//      "-source:3.0-migration",
      scala3cLanguageOptions,
      "-Ykind-projector",
//      "-siteroot",
      "./dotty-docs",
    ) ++ options
  } else {
    options
  }

def subProject(projectName: String): Project = {
  val prefixedName = prefixedProjectName(projectName)
  Project(projectName, file(s"modules/$prefixedName"))
    .settings(
      name                                    := prefixedName,
      testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework")),
      libraryDependencies ++= libs.hedgehogLibs,
      scalacOptions                           := scalacOptionsPostProcess(scalaVersion.value, scalacOptions.value).distinct,
      Compile / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src/main"
        if (scalaVersion.value.startsWith("2."))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
          )
        else
          Seq.empty
      },
      /* WartRemover and scalacOptions { */
      //      , Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value)
      //      , Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value)
      wartremoverErrors ++= commonWarts((update / scalaVersion).value),
      //            , wartremoverErrors ++= Warts.all
      Compile / console / wartremoverErrors   := List.empty,
      Compile / console / wartremoverWarnings := List.empty,
      Compile / console / scalacOptions       :=
        (console / scalacOptions)
          .value
          .distinct
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / wartremoverErrors      := List.empty,
      Test / console / wartremoverWarnings    := List.empty,
      Test / console / scalacOptions          :=
        (console / scalacOptions)
          .value
          .distinct
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      //      , Compile / compile / wartremoverExcluded += sourceManaged.value
      //      , Test / compile / wartremoverExcluded += sourceManaged.value
      /* } WartRemover and scalacOptions */
      licenses                                := List("MIT" -> url("http://opensource.org/licenses/MIT"))
    )
}
