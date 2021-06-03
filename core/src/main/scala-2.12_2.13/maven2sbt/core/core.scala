package maven2sbt

import cats._
import io.estatico.newtype.macros._
import io.estatico.newtype.ops._
import just.fp.Named


/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
package object core {

  @newtype case class GroupId(value: String)

  object GroupId {

    implicit val eq: Eq[GroupId] = deriving

    implicit val show: Show[GroupId] = deriving

    implicit val named: Named[GroupId] = Named.named("organization")

    implicit val groupIdRender: Render[GroupId] =
      Render.namedRender("groupId", GroupId.render)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(groupId: GroupId): Some[String] =
      Some(groupId.coerce)

    implicit final class GroupIdOps(val groupId: GroupId) extends AnyVal {
      def render(propsName: Props.PropsName): RenderedString =
        GroupId.render(propsName, groupId)
    }

    def render(propsName: Props.PropsName, groupId: GroupId): RenderedString =
      StringUtils.renderWithProps(propsName, groupId.value)
  }

  @newtype case class ArtifactId(value: String)

  object ArtifactId {

    implicit val eq: Eq[ArtifactId] = deriving

    implicit val show: Show[ArtifactId] = deriving

    implicit val named: Named[ArtifactId] = Named.named("name")

    implicit val artifactIdRender: Render[ArtifactId] =
      Render.namedRender("artifactId", ArtifactId.render)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(artifactId: ArtifactId): Some[String] =
      Some(artifactId.coerce)

    def render(propsName: Props.PropsName, artifactId: ArtifactId): RenderedString =
      StringUtils.renderWithProps(propsName, artifactId.value)

  }

  @newtype case class Version(value: String)

  object Version {

    implicit val eq: Eq[Version] = deriving

    implicit val show: Show[Version] = deriving

    implicit val named: Named[Version] = Named.named("version")

    implicit val versionRender: Render[Version] =
      Render.namedRender("version", Version.render)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(version: Version): Some[String] =
      Some(version.coerce)

    def render(propsName: Props.PropsName, version: Version): RenderedString =
      StringUtils.renderWithProps(propsName, version.value)

  }

  @newtype case class ScalaVersion(value: String)

  object ScalaVersion {

    implicit val eq: Eq[ScalaVersion] = deriving

    implicit val show: Show[ScalaVersion] = deriving

    implicit val named: Named[ScalaVersion] = Named.named("scalaVersion")

    implicit val scalaVersionRender: Render[ScalaVersion] =
      Render.namedRender("scalaVersion", ScalaVersion.render)

    def unapply(scalaVersion: ScalaVersion): Option[String] =
      Option(scalaVersion.coerce)

    def render(propsName: Props.PropsName, scalaVersion: ScalaVersion): RenderedString =
      StringUtils.renderWithProps(propsName, scalaVersion.value)

  }

  @newtype case class ScalaBinaryVersion(value: String)
  object ScalaBinaryVersion {
    @newtype case class Name(value: String)
  }

  implicit final class RenderedStringOps(val s: RenderedString) extends AnyVal {
    def toQuotedString: String =
      StringUtils.quoteRenderedString(s)

    def innerValue: String = s match {
      case RenderedString.WithProps(s) => s
      case RenderedString.WithoutProps(s) => s
      case RenderedString.NoQuotesRequired(s) => s
    }
  }

  @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
  implicit final class MkStringForOnlyStrings(val stringList: Seq[String]) extends AnyVal {
    def stringsMkString: String =
      stringList.mkString

    def stringsMkString(sep: String): String =
      stringList.mkString(sep)

    def stringsMkString(start: String, sep: String, end: String): String =
      stringList.mkString(start, sep, end)
  }

}