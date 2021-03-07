package maven2sbt.core

import cats.Show
import cats.syntax.show._
import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions
import scala.xml.Node

/**
 * @author Kevin Lee
 * @since 2021-03-04
 */
final case class Libs(dependencies: List[(Libs.LibValName, Dependency)])

object Libs {
  @newtype case class LibsName(libsName: String)
  
  @newtype case class LibValName(libValName: String)

  implicit val show: Show[Libs] = libs =>
    s"""Libs(dependencies =
       |  ${libs.dependencies.map { case (libValName, dependency) =>
            s"(libValName = ${libValName.libValName}, ${dependency.show})"
          }.mkString("  [\n      ","\n      ", "\n    ]")}
       |  )""".stripMargin

  def toValName(dependency: Dependency): LibValName =
    LibValName(StringUtils.capitalizeAfterIgnoringNonAlphaNumUnderscore(dependency.artifactId.artifactId))
  
  def from(pom: Node, scalaBinaryVersionName: Option[ScalaBinaryVersion.Name]): Libs =
    Libs(
      (for {
        dependencies <- (pom \ "dependencyManagement")
        dependency <- Dependency.from(dependencies, scalaBinaryVersionName)
      } yield (toValName(dependency), dependency)).toList
    )


  def render(propsName: Props.PropsName, libsName: LibsName, indentSize: Int, libs: Libs): String = {
    val indent = StringUtils.indent(indentSize)
    libs.dependencies
      .map(Libs.renderReusable(propsName, _))
        .map { case (name, libStr) =>
          s"${indent}val ${name.libValName} = ${libStr.toQuotedString}"
        }
      .stringsMkString(s"lazy val ${libsName.libsName} = new {\n", "\n", "\n}")

  }


  def renderReusable(
    propsName: Props.PropsName,
    dependencyWithValName: (Libs.LibValName, Dependency)
  ): (Libs.LibValName, RenderedString) = dependencyWithValName match {
    case (libValName, Dependency.Scala(groupId, artifactId, version, scope, exclusions)) =>
      val groupIdStr = Render[GroupId].render(propsName, groupId).toQuotedString
      val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
      val versionStr = Render[Version].render(propsName, version).toQuotedString
      (
        libValName
        , RenderedString.noQuotesRequired(
        s"""$groupIdStr %% $artifactIdStr % $versionStr${Scope.renderNonCompileWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"""
      )
      )
    case (libValName, Dependency.Java(groupId, artifactId, version, scope, exclusions)) =>
      val groupIdStr = Render[GroupId].render(propsName, groupId).toQuotedString
      val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
      val versionStr = Render[Version].render(propsName, version).toQuotedString
      (
        libValName
        , RenderedString.noQuotesRequired(
        s"""$groupIdStr % $artifactIdStr % $versionStr${Scope.renderNonCompileWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"""
      )
      )
  }

}
