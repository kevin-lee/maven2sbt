package maven2sbt.core

import just.fp.{Named, Render}

import scala.language.postfixOps
import scala.xml.Elem

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
    Render.namedRender("dependency", dependency => Dependency.render(dependency))

  def from(pom: Elem): Seq[Dependency] =
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

  def render(dependency: Dependency): String = dependency match {
    case Dependency(GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) =>
      val propsName = Props.PropsName("props")
      val groupIdStr = StringUtils.toPropertyNameOrItself(propsName, groupId)
      val artifactIdStr = StringUtils.toPropertyNameOrItself(propsName, artifactId)
      val versionStr = StringUtils.toPropertyNameOrItself(propsName, version)
      s"""$groupIdStr % $artifactIdStr % $versionStr${Scope.renderWithPrefix(" % ", scope)}${Exclusion.renderExclusions(propsName, exclusions)}"""
  }

}