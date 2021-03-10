package maven2sbt

import just.fp.Named

import cats._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
package object core {

  opaque type GroupId = String

  object GroupId {

    implicit val eq: Eq[GroupId] = Eq.fromUniversalEquals[GroupId]

    def apply(groupId: String): GroupId = groupId
    extension (groupId: GroupId) def value: String = groupId

    implicit val show: Show[GroupId] = _.value

    implicit val named: Named[GroupId] = Named.named("organization")

    implicit val groupIdRender: Render[GroupId] =
      Render.namedRender("groupId", GroupId.render)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(groupId: GroupId): Some[String] =
      Some(groupId.value)

    def render(propsName: Props.PropsName, groupId: GroupId): RenderedString =
      StringUtils.renderWithProps(propsName, groupId.value)

  }


  opaque type ArtifactId = String
  object ArtifactId {
    def apply(artifactId: String): ArtifactId = artifactId

    extension (artifactId: ArtifactId) def value: String = artifactId

    implicit val eq: Eq[ArtifactId] = Eq.fromUniversalEquals[ArtifactId]

    implicit val show: Show[ArtifactId] = _.value

    implicit val named: Named[ArtifactId] = Named.named("name")

    implicit val artifactIdRender: Render[ArtifactId] =
      Render.namedRender("artifactId", ArtifactId.render)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(artifactId: ArtifactId): Some[String] =
      Some(artifactId.value)

    def render(propsName: Props.PropsName, artifactId: ArtifactId): RenderedString =
      StringUtils.renderWithProps(propsName, artifactId.value)

  }

  opaque type Version = String
  object Version {
    def apply(version: String): Version = version

    extension (version: Version) def value: String = version

    implicit val eq: Eq[Version] = Eq.fromUniversalEquals[Version]

    implicit val show: Show[Version] = _.value

    implicit val named: Named[Version] = Named.named("version")

    implicit val versionRender: Render[Version] =
      Render.namedRender("version", Version.render)

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(version: Version): Some[String] =
      Some(version.value)

    def render(propsName: Props.PropsName, version: Version): RenderedString =
      StringUtils.renderWithProps(propsName, version.value)

  }

  opaque type ScalaVersion = String
  object ScalaVersion {
    def apply(scalaVersion: String): ScalaVersion = scalaVersion

    extension (scalaVersion: ScalaVersion) {
      def value: String = scalaVersion
    }

    implicit val eq: Eq[ScalaVersion] = Eq.fromUniversalEquals[ScalaVersion]

    implicit val show: Show[ScalaVersion] = _.value

    implicit val named: Named[ScalaVersion] = Named.named("scalaVersion")

    implicit val scalaVersionRender: Render[ScalaVersion] =
      Render.namedRender("scalaVersion", ScalaVersion.render)

    def unapply(scalaVersion: ScalaVersion): Option[String] =
      Option(scalaVersion.value)

    def render(propsName: Props.PropsName, scalaVersion: ScalaVersion): RenderedString =
      StringUtils.renderWithProps(propsName, scalaVersion.value)

  }

  opaque type ScalaBinaryVersion = String
  object ScalaBinaryVersion {
    def apply(scalaBinaryVersion: String): ScalaBinaryVersion = scalaBinaryVersion

    extension (scalaBinaryVersion: ScalaBinaryVersion) def value: String = scalaBinaryVersion

    opaque type Name = String
    object Name {
      def apply(name: String): Name = name

      extension (name: Name) def value: String = name

    }
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