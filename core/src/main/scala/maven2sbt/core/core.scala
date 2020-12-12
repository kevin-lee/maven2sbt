package maven2sbt

import io.estatico.newtype.macros._
import io.estatico.newtype.ops._
import just.fp.{Named, Render}

import scala.language.implicitConversions

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
package object core {

  @newtype case class GroupId(groupId: String)

  object GroupId {

    implicit val named: Named[GroupId] = Named.named("organization")

    implicit val render: Render[GroupId] =
      Render.namedRender("groupId", groupId => groupId.groupId)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(groupId: GroupId): Some[String] =
      Some(groupId.coerce)

  }

  @newtype case class ArtifactId(artifactId: String)

  object ArtifactId {

    implicit val named: Named[ArtifactId] = Named.named("name")

    implicit val render: Render[ArtifactId] =
      Render.namedRender("artifactId", artifactId => artifactId.artifactId)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(artifactId: ArtifactId): Some[String] =
      Some(artifactId.coerce)

  }

  @newtype case class Version(version: String)

  object Version {

    implicit val named: Named[Version] = Named.named("version")

    implicit val render: Render[Version] =
      Render.namedRender("version", version => version.version)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(version: Version): Some[String] =
      Some(version.coerce)

  }

  @newtype case class ScalaVersion(scalaVersion: String)

  object ScalaVersion {

    implicit val named: Named[ScalaVersion] = Named.named("scalaVersion")

    implicit val render: Render[ScalaVersion] =
      Render.namedRender("scalaVersion", scalaVersion => scalaVersion.scalaVersion)

    def unapply(scalaVersion: ScalaVersion): Option[String] =
      Option(scalaVersion.coerce)

  }

}