package maven2sbt.core

import just.fp.{Named, Render}

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */

final case class GroupId(groupId: String) extends AnyVal
object GroupId extends (String => GroupId) {
  implicit val named: Named[GroupId] = Named.named("organization")

  implicit val render: Render[GroupId] =
    Render.namedRender("groupId", groupId => groupId.groupId)
}

final case class ArtifactId(artifactId: String) extends AnyVal
object ArtifactId extends (String => ArtifactId) {
  implicit val named: Named[ArtifactId] = Named.named("name")

  implicit val render: Render[ArtifactId] =
    Render.namedRender("artifactId", artifactId => artifactId.artifactId)
}

final case class Version(version: String) extends AnyVal
object Version extends (String => Version) {
  implicit val named: Named[Version] = Named.named("version")

  implicit val render: Render[Version] =
    Render.namedRender("version", version => version.version)
}

final case class ScalaVersion(scalaVersion: String) extends AnyVal
object ScalaVersion extends (String => ScalaVersion) {
  implicit val named: Named[ScalaVersion] = Named.named("scalaVersion")

  implicit val render: Render[ScalaVersion] =
    Render.namedRender("scalaVersion", scalaVersion => scalaVersion.scalaVersion)
}
