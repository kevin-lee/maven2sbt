package maven2sbt.core

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
sealed trait Dependency

object Dependency extends DependencyPlus {

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

    def scope: Scope = Dependency.scope(dependency)

    def exclusions: List[Exclusion] = Dependency.exclusions(dependency)

    def isScalaLib: Boolean = Dependency.isScalaLib(dependency)

    def isJavaLib: Boolean = Dependency.isJavaLib(dependency)

    def tupled: (GroupId, ArtifactId, Version, Scope, List[Exclusion]) =
      Dependency.tupled(dependency)
  }

}