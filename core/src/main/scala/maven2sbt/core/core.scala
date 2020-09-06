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

    def unapply(groupId: GroupId): Option[String] =
      Option(groupId.coerce)

  }

  @newtype case class ArtifactId(artifactId: String)

  object ArtifactId {

    implicit val named: Named[ArtifactId] = Named.named("name")

    implicit val render: Render[ArtifactId] =
      Render.namedRender("artifactId", artifactId => artifactId.artifactId)

    def unapply(artifactId: ArtifactId): Option[String] =
      Option(artifactId.coerce)

  }

  @newtype case class Version(version: String)

  object Version {

    implicit val named: Named[Version] = Named.named("version")

    implicit val render: Render[Version] =
      Render.namedRender("version", version => version.version)

    def unapply(version: Version): Option[String] =
      Option(version.coerce)

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