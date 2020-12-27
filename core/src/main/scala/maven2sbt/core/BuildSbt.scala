package maven2sbt.core

import BuildSbt._
import cats.syntax.all._
import just.fp.{Named, Render}

/**
 * @author Kevin Lee
 * @since 2020-01-29
 */
final case class BuildSbt(
    globalSettings: GlobalSettings
  , thisBuildSettings: ThisBuildSettings
  , projectSettings: ProjectSettings
  , props: List[Prop]
  )

object BuildSbt {
  import StringUtils._

  final case class Settings(
      groupId: Option[GroupId]
    , artifactId: Option[ArtifactId]
    , version: Option[Version]
    , scalaVersion: Option[ScalaVersion]
    , repositories: List[Repository]
    , dependencies: List[Dependency]
    )

  def toFieldValue[A: Named : Render](prefix: Option[String], a: A): String =
    s"""${prefix.getOrElse("")}${Named[A].name} := "${Render[A].render(a)}""""

  def renderListOfFieldValue[A: Named: Render](
    prefix: Option[String],
    as: List[A],
    indentSize: Int
  ): Option[String] =
    as match {
      case Nil =>
        none[String]

      case x :: Nil =>
        s"${Named[A].name} += ${Render[A].render(x)}".some

      case x :: xs =>
        val idt = indent(indentSize)
        s"""${prefix.getOrElse("")}${Named[A].name} ++= List(
           |$idt  ${Render[A].render(x)},
           |$idt  ${xs.map(Render[A].render).mkString(s",\n$idt  ")}
           |$idt)""".stripMargin.some
    }

  object Settings {
    def render(settings: Settings, prefix: Option[String], delimiter: String, indentSize: Int): String =
      (
        settings.groupId.map(groupId => toFieldValue(prefix, groupId)).toList ++
        settings.version.map(version => toFieldValue(prefix, version)).toList ++
        settings.scalaVersion.map(scalaVersion => toFieldValue(prefix, scalaVersion)).toList ++
        settings.artifactId.map(artifactId => toFieldValue(prefix, artifactId)).toList ++
        renderListOfFieldValue(prefix, settings.repositories, indentSize).toList ++
        renderListOfFieldValue(prefix, settings.dependencies, indentSize).toList
      )
      .mkString(delimiter)
  }

  final case class ProjectSettings(projectSettings: Settings) extends AnyVal
  object ProjectSettings {
    def render(projectSettings: ProjectSettings): String =
      Settings.render(projectSettings.projectSettings, none[String], s",\n${indent(4)}", 4)

  }
  final case class ThisBuildSettings(thisBuildSettings: Settings) extends AnyVal
  object ThisBuildSettings {
    def render(thisBuildSettings: ThisBuildSettings): String =
      Settings.render(thisBuildSettings.thisBuildSettings, "ThisBuild / ".some, "\n", 2)
  }
  final case class GlobalSettings(globalSettings: Settings) extends AnyVal
  object GlobalSettings {

    def empty: GlobalSettings =
      GlobalSettings(Settings(
          none[GroupId]
        , none[ArtifactId]
        , none[Version]
        , none[ScalaVersion]
        , List.empty[Repository]
        , List.empty[Dependency]
        )
      )

    def render(globalSettings: GlobalSettings): String =
      Settings.render(globalSettings.globalSettings, "Global / ".some, "\n", 2)
  }

  def render(buildSbt: BuildSbt): String = buildSbt match {
    case BuildSbt(
        globalSettings
      , thisBuildSettings
      , projectSettings
      , props
      ) =>

      val globalSettingsRendered = GlobalSettings.render(globalSettings)
      val thisBuildSettingsRendered = ThisBuildSettings.render(thisBuildSettings)
      val projectSettingsRendered = ProjectSettings.render(projectSettings)

      s"""
         |${Props.renderProps(Props.PropsName("props"), 2, props)}
         |
         |$globalSettingsRendered
         |$thisBuildSettingsRendered
         |lazy val root = (project in file("."))
         |  .settings(
         |    $projectSettingsRendered
         |  )
         |""".stripMargin
  }
}
