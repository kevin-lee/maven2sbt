package maven2sbt.core

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */

final case class GroupId(groupId: String) extends AnyVal
final case class ArtifactId(artifactId: String) extends AnyVal
final case class Version(version: String) extends AnyVal
final case class ScalaVersion(scalaVersion: String) extends AnyVal
