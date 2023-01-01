package maven2sbt.core

import cats.Show
import cats.syntax.all.*
import just.fp.Named

import scala.language.postfixOps
import scala.xml.Node

/** @author Kevin Lee
  * @since 2021-03-11
  */
trait DependencyPlus { self =>

  implicit val dependencyShow: Show[Dependency] = {
    case Dependency.Scala(groupId, artifactId, version, scope, exclusions) =>
      show"Dependency.Scala($groupId, $artifactId, $version, $scope, ${exclusions.show})"
    case Dependency.Java(groupId, artifactId, version, scope, exclusions) =>
      show"Dependency.Java($groupId, $artifactId, $version, $scope, ${exclusions.show})"
  }

  def artifactId(dependency: Dependency): ArtifactId = dependency match {
    case Dependency.Scala(_, artifactId, _, _, _) =>
      artifactId
    case Dependency.Java(_, artifactId, _, _, _) =>
      artifactId
  }

  def scope(dependency: Dependency): Scope = dependency match {
    case Dependency.Scala(_, _, _, scope, _) =>
      scope
    case Dependency.Java(_, _, _, scope, _) =>
      scope
  }

  def exclusions(dependency: Dependency): List[Exclusion] = dependency match {
    case Dependency.Scala(_, _, _, _, exclusions) =>
      exclusions
    case Dependency.Java(_, _, _, _, exclusions) =>
      exclusions
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

  implicit val namedDependency: Named[Dependency]             = Named.named("libraryDependencies")
  implicit val renderDependency: ReferencedRender[Dependency] =
    ReferencedRender.namedReferencedRender(
      "dependency",
      (propsName, libsName, libs, dependency) => self.render(propsName, libsName, libs, dependency)
    )

  def from(pom: Node, scalaBinaryVersionName: Option[ScalaBinaryVersion.Name]): Seq[Dependency] =
    pom \ "dependencies" \ "dependency" map { dependency =>
      val groupId    = dependency \ "groupId" text
      val artifactId = dependency \ "artifactId" text
      val version    = dependency \ "version" text
      val scope      = dependency \ "scope" text

      val exclusions: List[Exclusion] = (dependency \ "exclusions" \ "exclusion").map { exclusion =>
        val groupId    = (exclusion \ "groupId").text
        val artifactId = (exclusion \ "artifactId").text

        Exclusion(GroupId(groupId), ArtifactId(artifactId))
      }.toList

      scalaBinaryVersionName match {
        case Some(scalaBinaryVersionName) =>
          val suffix = s"_$${${scalaBinaryVersionName.value}}"
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
          Dependency.java(
            GroupId(groupId),
            ArtifactId(artifactId),
            Version(version),
            Option(scope).fold(Scope.default)(Scope.parseUnsafe),
            exclusions
          )

      }
    }

  def render(
    propsName: Props.PropsName,
    libsName: Libs.LibsName,
    libs: Libs,
    dependency: Dependency
  ): RenderedString = {

    def renderWithoutLibs(propsName: Props.PropsName, dependency: Dependency): RenderedString =
      dependency match {
        case Dependency.Scala(groupId, artifactId, version, scope, exclusions) =>
          val groupIdStr    = Render[GroupId].render(propsName, groupId).toQuotedString
          val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
          val versionStr    = Render[Version].render(propsName, version).toQuotedString
          RenderedString.noQuotesRequired(
            s"""$groupIdStr %% $artifactIdStr % $versionStr${Scope
                .renderNonCompileWithPrefix(" % ", scope)}${Render[List[Exclusion]]
                .render(propsName, exclusions)
                .toQuotedString}"""
          )

        case Dependency.Java(groupId, artifactId, version, scope, exclusions) =>
          val groupIdStr    = Render[GroupId].render(propsName, groupId).toQuotedString
          val artifactIdStr = Render[ArtifactId].render(propsName, artifactId).toQuotedString
          val versionStr    = Render[Version].render(propsName, version).toQuotedString
          RenderedString.noQuotesRequired(
            s"""$groupIdStr % $artifactIdStr % $versionStr${Scope
                .renderNonCompileWithPrefix(" % ", scope)}${Render[List[Exclusion]]
                .render(propsName, exclusions)
                .toQuotedString}"""
          )
      }

    val refLibFound = libs.dependencies.find {
      case (_, dep) =>
        val (libGroupId, libArtifactId, _, _, _) = dep.tupled
        val (groupId, artifactId, _, _, _)       = dependency.tupled
        libGroupId === groupId && libArtifactId === artifactId
    }

    refLibFound match {
      case Some((libValName, libDep)) =>
        (libDep.tupled, dependency.tupled) match {
          case (
                (_, _, libVersion, Scope.Compile | Scope.Default, libExclusions),
                (_, _, version, scope, exclusions)
              ) if version.value.isBlank || libVersion === version =>
            if ((scope === Scope.Compile || scope === Scope.Default)) {
              if (libExclusions.length === exclusions.length && libExclusions.diff(exclusions).isEmpty)
                RenderedString.noQuotesRequired(s"${libsName.value}.${libValName.value}")
              else if (libExclusions.isEmpty)
                RenderedString.noQuotesRequired(
                  s"${libsName.value}.${libValName.value}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"
                )
              else
                renderWithoutLibs(propsName, dependency)
            } else {
              if (libExclusions.length === exclusions.length && libExclusions.diff(exclusions).isEmpty)
                RenderedString.noQuotesRequired(
                  s"""${libsName.value}.${libValName.value}${Scope.renderNonCompileWithPrefix(" % ", scope)}"""
                )
              else if (libExclusions.isEmpty)
                RenderedString.noQuotesRequired(
                  s"${libsName.value}.${libValName.value}${Scope
                      .renderNonCompileWithPrefix(" % ", scope)}${Render[List[Exclusion]].render(propsName, exclusions).toQuotedString}"
                )
              else
                renderWithoutLibs(propsName, dependency)
            }

          case ((_, _, _, Scope.Compile | Scope.Default, _), (_, _, _, _, _)) =>
            renderWithoutLibs(propsName, dependency)

          case ((_, _, libVersion, libScope, libExclusions), (_, _, version, scope, exclusions))
              if version.value.isBlank || libVersion === version =>
            if (libScope === scope) {
              if (libExclusions.length === exclusions.length && libExclusions.diff(exclusions).isEmpty)
                RenderedString.noQuotesRequired(s"${libsName.value}.${libValName.value}")
              else if (libExclusions.isEmpty)
                RenderedString.noQuotesRequired(
                  s"""${libsName.value}.${libValName.value}${Render[List[Exclusion]]
                      .render(propsName, exclusions)
                      .toQuotedString}"""
                )
              else
                renderWithoutLibs(propsName, dependency)
            } else {
              renderWithoutLibs(propsName, dependency)
            }

          case ((_, _, _, _, _), (_, _, _, _, _)) =>
            renderWithoutLibs(propsName, dependency)

        }
      case None =>
        renderWithoutLibs(propsName, dependency)
    }
  }

}
