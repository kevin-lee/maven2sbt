package maven2sbt.core

import just.fp.{Named, Render}

import scala.language.postfixOps
import scala.xml.Node

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class Dependency(
    groupId: GroupId
  , artifactId: ArtifactId
  , version: Version
  , scope: Scope
  , exclusions: List[Exclusion]
  )

object Dependency {

  implicit val namedDependency: Named[Dependency] = Named.named("libraryDependencies")
  implicit val renderDependency: Render[Dependency] =
    Render.namedRender("dependency", (propsName, dependency) => Dependency.render(propsName, dependency))

  def from(pom: Node): Seq[Dependency] =
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

      Dependency(
          GroupId(groupId)
        , ArtifactId(artifactId)
        , Version(version)
        , Option(scope).fold(Scope.default)(Scope.parseUnsafe)
        , exclusions
        )
    }

  def render(propsName: Props.PropsName, dependency: Dependency): RenderedString = dependency match {
    case Dependency(groupId, artifactId, version, scope, exclusions) =>
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
    case (libValName, Dependency(groupId, artifactId, version, scope, exclusions)) =>
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