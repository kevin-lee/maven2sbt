package maven2sbt.core

import BuildSbt._
import cats.syntax.all._
import just.fp.Named

/**
 * @author Kevin Lee
 * @since 2020-01-29
 */
final case class BuildSbt(
    globalSettings: GlobalSettings
  , thisBuildSettings: ThisBuildSettings
  , projectSettings: ProjectSettings
  , props: List[Prop]
  , libs: Libs
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

  def toFieldValue[A: Named : Render](prefix: Option[String], propsName: Props.PropsName, a: A): String =
    s"""${prefix.getOrElse("")}${Named[A].name} := ${Render[A].render(propsName, a).toQuotedString}"""

  def renderListOfFieldValue[A: Named: Render](
    prefix: Option[String],
    propsName: Props.PropsName,
    as: List[A],
    indentSize: Int
  ): Option[String] =
    as match {
      case Nil =>
        none[String]

      case x :: Nil =>
        s"${Named[A].name} += ${Render[A].render(propsName, x).toQuotedString}".some

      case x :: xs =>
        val idt = indent(indentSize)
        s"""${prefix.getOrElse("")}${Named[A].name} ++= List(
           |$idt  ${Render[A].render(propsName, x).toQuotedString},
           |$idt  ${xs.map(eachX => Render[A].render(propsName, eachX).toQuotedString).stringsMkString(s",\n$idt  ")}
           |$idt)""".stripMargin.some
    }

  object Settings {
    def render(settings: Settings, prefix: Option[String], propsName: Props.PropsName, delimiter: String, indentSize: Int): String =
      (
        settings.groupId.map(groupId => toFieldValue(prefix, propsName, groupId)).toList ++
        settings.version.map(version => toFieldValue(prefix, propsName, version)).toList ++
        settings.scalaVersion.map(scalaVersion => toFieldValue(prefix, propsName, scalaVersion)).toList ++
        settings.artifactId.map(artifactId => toFieldValue(prefix, propsName, artifactId)).toList ++
        renderListOfFieldValue(prefix, propsName, settings.repositories, indentSize).toList ++
        renderListOfFieldValue(prefix, propsName, settings.dependencies, indentSize).toList
      )
      .stringsMkString(delimiter)
  }

  final case class ProjectSettings(projectSettings: Settings) extends AnyVal
  object ProjectSettings {
    def render(propsName: Props.PropsName, projectSettings: ProjectSettings): String =
      Settings.render(projectSettings.projectSettings, none[String], propsName, s",\n${indent(4)}", 4)

  }
  final case class ThisBuildSettings(thisBuildSettings: Settings) extends AnyVal
  object ThisBuildSettings {
    def render(propsName: Props.PropsName, thisBuildSettings: ThisBuildSettings): String =
      Settings.render(thisBuildSettings.thisBuildSettings, "ThisBuild / ".some, propsName, "\n", 2)
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

    def render(propsName: Props.PropsName, globalSettings: GlobalSettings): String =
      Settings.render(globalSettings.globalSettings, "Global / ".some, propsName, "\n", 2)
  }

  def render(buildSbt: BuildSbt, propsName: Props.PropsName, libsName: Libs.LibsName): String = buildSbt match {
    case BuildSbt(
        globalSettings
      , thisBuildSettings
      , projectSettings
      , props
      , libs
      ) =>

      val globalSettingsRendered = GlobalSettings.render(propsName, globalSettings)
      val thisBuildSettingsRendered = ThisBuildSettings.render(propsName, thisBuildSettings)
      val projectSettingsRendered = ProjectSettings.render(propsName, projectSettings)

      s"""
         |$globalSettingsRendered
         |$thisBuildSettingsRendered
         |lazy val root = (project in file("."))
         |  .settings(
         |    $projectSettingsRendered
         |  )
         |
         |${Props.renderProps(propsName, 2, props)}
         |
         |${Libs.render(propsName, libsName, 2, libs)}
         |""".stripMargin
  }
}
