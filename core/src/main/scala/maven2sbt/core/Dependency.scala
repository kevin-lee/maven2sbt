package maven2sbt.core

import just.fp.{Named, Render}

import scala.language.postfixOps
import scala.xml.Node

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
sealed trait Dependency

object Dependency {

  final case class Scala(
    groupId: GroupId,
    artifactId: ArtifactId,
    version: Version,
    scope: Scope,
    exclusions: List[Exclusion]
  ) extends Dependency

  final case class Java(
    groupId: GroupId,
    artifactId: ArtifactId,
    version: Version,
    scope: Scope,
    exclusions: List[Exclusion]
  ) extends Dependency

  def scala(
    groupId: GroupId,
    artifactId: ArtifactId,
    version: Version,
    scope: Scope,
    exclusions: List[Exclusion]
  ): Dependency = Scala(groupId, artifactId, version, scope, exclusions)

  def java(
    groupId: GroupId,
    artifactId: ArtifactId,
    version: Version,
    scope: Scope,
    exclusions: List[Exclusion]
  ): Dependency = Java(groupId, artifactId, version, scope, exclusions)


  implicit final class DependencyOps(val dependency: Dependency) extends AnyVal {
    def artifactId: ArtifactId = Dependency.artifactId(dependency)

    def isScalaLib: Boolean = Dependency.isScalaLib(dependency)

    def isJavaLib: Boolean = Dependency.isJavaLib(dependency)

    def tupled: (GroupId, ArtifactId, Version, Scope, List[Exclusion]) =
      Dependency.tupled((dependency))
  }

  def artifactId(dependency: Dependency): ArtifactId = dependency match {
    case Scala(_, artifactId, _, _, _) =>
      artifactId
    case Java(_, artifactId, _, _, _) =>
      artifactId
  }

  def isScalaLib(dependency: Dependency): Boolean = dependency match {
    case _: Dependency.Scala => true
    case _: Dependency.Java => false
  }

  def isJavaLib(dependency: Dependency): Boolean = !isScalaLib(dependency)

  def tupled(dependency: Dependency): (GroupId, ArtifactId, Version, Scope, List[Exclusion]) =
    dependency match {
      case Dependency.Scala(groupId, artifactId, version, scope, exclusions) =>
        (groupId, artifactId, version, scope, exclusions)
      case Dependency.Java(groupId, artifactId, version, scope, exclusions) =>
        (groupId, artifactId, version, scope, exclusions)
    }


  implicit val namedDependency: Named[Dependency] = Named.named("libraryDependencies")
  implicit val renderDependency: Render[Dependency] =
    Render.namedRender("dependency", (propsName, dependency) => Dependency.render(propsName, dependency))

  def from(pom: Node, scalaBinaryVersionName: Option[ScalaBinaryVersion.Name]): Seq[Dependency] =
    pom \ "dependencies" \ "dependency" map { dependency =>
      val groupId = dependency \ "groupId" text
      val artifactId = dependency \ "artifactId" text
      val version = dependency \ "version" text
      val scope = dependency \ "scope" text

      val exclusions: List[Exclusion] = (dependency \ "exclusions" \ "exclusion").map { exclusion =>
        val groupId = (exclusion \ "groupId").text
        val artifactId = (exclusion \ "artifactId").text

        Exclusion(GroupId(groupId), ArtifactId(artifactId))
      }.toList

      scalaBinaryVersionName match {
        case Some(scalaBinaryVersionName) =>
          val suffix = s"_$${${scalaBinaryVersionName.name}}"
          if (artifactId.endsWith(suffix)) {
            Dependency.scala(
              GroupId(groupId),
              ArtifactId(artifactId.substring(0, artifactId.length - suffix.length)),
              Version(version),
              Option(scope).fold(Scope.default)(Scope.parseUnsafe),
              exclusions
            )
          } else {
            Dependency.java(
              GroupId(groupId),
              ArtifactId(artifactId),
              Version(version),
              Option(scope).fold(Scope.default)(Scope.parseUnsafe),
              exclusions
            )
          }
        case None =>
          Dependency.java (
            GroupId(groupId),
            ArtifactId(artifactId),
            Version(version),
            Option(scope).fold(Scope.default)(Scope.parseUnsafe),
            exclusions
          )

      }
    }

  def render(propsName: Props.PropsName, dependency: Dependency): RenderedString = dependency match {
    case Dependency.Scala(groupId, artifactId, version, scope, exclusions) =>
      val groupIdStr = Render[GroupId].render(propsName, groupId).toQuotedString
      val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
      val versionStr = Render[Version].render(propsName, version).toQuotedString
      RenderedString.noQuotesRequired(
        s"""$groupIdStr %% $artifactIdStr % $versionStr${Scope.renderWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"""
      )

    case Dependency.Java(groupId, artifactId, version, scope, exclusions) =>
      val groupIdStr = Render[GroupId].render(propsName, groupId).toQuotedString
      val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
      val versionStr = Render[Version].render(propsName, version).toQuotedString
      RenderedString.noQuotesRequired(
        s"""$groupIdStr % $artifactIdStr % $versionStr${Scope.renderWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"""
      )
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
          s"""$groupIdStr %% $artifactIdStr % $versionStr${Scope.renderWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"""
        )
      )
    case (libValName, Dependency.Java(groupId, artifactId, version, scope, exclusions)) =>
      val groupIdStr = Render[GroupId].render(propsName, groupId).toQuotedString
      val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
      val versionStr = Render[Version].render(propsName, version).toQuotedString
      (
        libValName
      , RenderedString.noQuotesRequired(
          s"""$groupIdStr % $artifactIdStr % $versionStr${Scope.renderWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"""
        )
      )
  }

}