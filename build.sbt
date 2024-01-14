import SbtProjectInfo._

ThisBuild / organization := "io.kevinlee"
ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / developers   := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}"),
  ),
)
ThisBuild / homepage     := url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}").some
ThisBuild / scmInfo      :=
  ScmInfo(
    url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
    s"https://github.com/${props.GitHubUsername}/${props.RepoName}.git",
  ).some
ThisBuild / licenses     := List("MIT" -> url("http://opensource.org/licenses/MIT"))

ThisBuild / resolvers += "sonatype-snapshots" at s"https://${props.SonatypeCredentialHost}/content/repositories/snapshots"

ThisBuild / scalafixConfig := (
  if (scalaVersion.value.startsWith("3"))
    ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
  else
    ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
)

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
  .settings(mavenCentralPublishSettings)
  .settings(noPublish)
  .aggregate(core, cli)

lazy val core = module("core")
  .enablePlugins(BuildInfoPlugin)
  .settings(
//    resolvers += Resolver.sonatypeRepo("snapshots"),
    crossScalaVersions  := props.CrossScalaVersions,
    libraryDependencies ++= {
      val scalaV = scalaVersion.value
      if (scalaV.startsWith("3"))
        List(libs.catsLib, libs.scalaXml) ++ libs.refined4s
      else
        List(libs.newTypeLib, libs.catsLib, libs.scalaXml)
    },
    libraryDependencies ++=
      libs.effectieAll ++
        libs.extrasAll,
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

lazy val cli = module("cli")
  .enablePlugins(JavaAppPackaging, NativeImagePlugin)
  .settings(
    libraryDependencies  := libraryDependenciesPostProcess(scalaVersion.value, libraryDependencies.value),
    scalaVersion         := (ThisBuild / scalaVersion).value,
    maintainer           := "Kevin Lee <kevin.code@kevinlee.io>",
    packageSummary       := "Maven2Sbt",
    packageDescription   := "A tool to convert Maven pom.xml into sbt build.sbt",
    executableScriptName := props.ExecutableScriptName,
    nativeImageVersion   := "22.3.0",
    nativeImageJvm       := "graalvm-java17",
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

    val GitHubUsername       = "kevin-lee"
    val RepoName             = "maven2sbt"
    val ExecutableScriptName = RepoName

    val SonatypeCredentialHost = "s01.oss.sonatype.org"
    val SonatypeRepository     = s"https://$SonatypeCredentialHost/service/local"

    val Scala2Version       = "2.13.10"
    val Scala3Version       = "3.2.2"
    val CrossScalaVersions  = List("2.12.17", Scala2Version, Scala3Version).distinct
//    val ProjectScalaVersion = Scala2Version
    val ProjectScalaVersion = Scala3Version

    val CatsVersion       = "2.9.0"
    val CatsEffectVersion = "3.5.2"

    val HedgehogVersion = "0.10.1"

//    val canEqualVersion = "0.1.0"

    val EffectieVersion = "2.0.0-beta14"
    val LoggerFVersion  = "2.0.0-beta24"

    val PirateVersion = "faff703b67fd10199098e806a3f6858982823461"
    val pirateUri     = uri(s"https://github.com/$GitHubUsername/pirate.git#$PirateVersion")

    val ScalaXml2Version = "2.1.0"

    val Refined4sVersion = "0.8.0"

    val ExtrasVersion = "0.44.0"

  }

lazy val libs =
  new {
    lazy val hedgehogLibs: List[ModuleID] = List(
      "qa.hedgehog" %% "hedgehog-core"   % props.HedgehogVersion,
      "qa.hedgehog" %% "hedgehog-runner" % props.HedgehogVersion,
      "qa.hedgehog" %% "hedgehog-sbt"    % props.HedgehogVersion,
    ).map(_ % Test)

//    lazy val canEqual = "io.kevinlee" %% "can-equal" % props.canEqualVersion

    lazy val scalaXml = "org.scala-lang.modules" %% "scala-xml" % props.ScalaXml2Version

    lazy val catsLib = "org.typelevel" %% "cats-core" % props.CatsVersion

    lazy val catsEffectLib = "org.typelevel" %% "cats-effect" % props.CatsEffectVersion

    lazy val effectieCore        = "io.kevinlee" %% "effectie-core"         % props.EffectieVersion
    lazy val effectieSyntax      = "io.kevinlee" %% "effectie-syntax"       % props.EffectieVersion
    lazy val effectieCatsEffect2 = "io.kevinlee" %% "effectie-cats-effect3" % props.EffectieVersion
    lazy val effectieAll         = List(
      effectieCore,
      effectieSyntax,
      effectieCatsEffect2,
    )

    lazy val newTypeLib = "io.estatico" %% "newtype" % "0.4.4"

    lazy val refined4s = List(
      "io.kevinlee" %% "refined4s-core" % props.Refined4sVersion,
      "io.kevinlee" %% "refined4s-cats" % props.Refined4sVersion,
    )

    lazy val extrasCats    = "io.kevinlee" %% "extras-cats"     % props.ExtrasVersion
    lazy val extrasScalaIo = "io.kevinlee" %% "extras-scala-io" % props.ExtrasVersion
    lazy val extrasAll     = List(extrasCats, extrasScalaIo)
  }

def libraryDependenciesPostProcess(
  scalaVersion: String,
  libraries: Seq[ModuleID],
): Seq[ModuleID] =
  if (scalaVersion.startsWith("3.")) {
    libraries
      .filterNot(removeDottyIncompatible)
  } else {
    libraries
  }

//lazy val scala3cLanguageOptions =
//  List(
//    "dynamics",
//    "existentials",
//    "higherKinds",
//    "reflectiveCalls",
//    "experimental.macros",
//    "implicitConversions",
//  ).map("-language:" + _)

def scalacOptionsPostProcess(scalaVersion: String, options: Seq[String]): Seq[String] =
  if (scalaVersion.startsWith("3.")) {
    options ++ List("-explain")
//    scala3cLanguageOptions ++
//      options.filterNot(o =>
//        o == "-language:dynamics,existentials,higherKinds,reflectiveCalls,experimental.macros,implicitConversions" || o == "UTF-8",
//      )
  } else if (scalaVersion.startsWith("2.13")) {
    "-Xsource:3" +: "-P:kind-projector:underscore-placeholders" +: "-Ymacro-annotations" +:
      options.filterNot(_ == "-Xlint:package-object-classes")
  } else {
    "-Xsource:3" +: "-P:kind-projector:underscore-placeholders" +:
      options.filterNot(_ == "-Xlint:package-object-classes")
  }

lazy val mavenCentralPublishSettings: SettingsDefinition = List(
  /* Publish to Maven Central { */
  sonatypeCredentialHost := props.SonatypeCredentialHost,
  sonatypeRepository     := props.SonatypeRepository,
  /* } Publish to Maven Central */
)

def module(projectName: String): Project = {
  val prefixedName = prefixedProjectName(projectName)
  Project(projectName, file(s"modules/$prefixedName"))
    .settings(
      name                           := prefixedName,
      libraryDependencies ++= libs.hedgehogLibs,
      scalacOptions                  := scalacOptionsPostProcess(scalaVersion.value, scalacOptions.value).distinct,
      scalafixConfig                 := (
        if (scalaVersion.value.startsWith("3"))
          ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
        else
          ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
      ),
      Compile / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src/main"
        if (scalaVersion.value.startsWith("2."))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
          )
        else
          Seq.empty
      },
      Test / compile / scalacOptions := (Test / compile / scalacOptions).value.filterNot(_ == "-Wnonunit-statement"),
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
      licenses                                := List("MIT" -> url("http://opensource.org/licenses/MIT")),
    )
}
