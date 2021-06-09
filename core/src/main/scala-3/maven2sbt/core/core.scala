package maven2sbt

import just.fp.Named

import cats.*

/** @author Kevin Lee
  * @since 2019-04-22
  */
package object core {

  opaque type GroupId = String

  object GroupId {

    def apply(groupId: String): GroupId = groupId

    given canEqualGroupId: CanEqual[GroupId, GroupId] = CanEqual.derived

    given eq: Eq[GroupId] = Eq.fromUniversalEquals[GroupId]

    extension (groupId: GroupId) {
      def value: String = groupId

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, groupId.value)
    }

    given show: Show[GroupId] = _.value

    given named: Named[GroupId] = Named.named("organization")

    given groupIdRender: Render[GroupId] =
      Render.namedRender("groupId", (propsName, groupId) => groupId.render(propsName))

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(groupId: GroupId): Some[String] =
      Some(groupId.value)

  }

  opaque type ArtifactId = String
  object ArtifactId {
    def apply(artifactId: String): ArtifactId = artifactId

    extension (artifactId: ArtifactId) {
      def value: String = artifactId

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, artifactId.value)

    }

    given eq: Eq[ArtifactId] = Eq.fromUniversalEquals[ArtifactId]

    given show: Show[ArtifactId] = _.value

    given named: Named[ArtifactId] = Named.named("name")

    given artifactIdRender: Render[ArtifactId] =
      Render.namedRender(
        "artifactId",
        (propsName, artifactId) => artifactId.render(propsName)
      )

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(artifactId: ArtifactId): Some[String] =
      Some(artifactId.value)

  }

  opaque type Version = String
  object Version {
    def apply(version: String): Version = version

    extension (version: Version) {
      def value: String = version

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, version.value)
    }

    given eq: Eq[Version] = Eq.fromUniversalEquals[Version]

    given show: Show[Version] = _.value

    given named: Named[Version] = Named.named("version")

    given versionRender: Render[Version] =
      Render.namedRender("version", (propsName, version) => version.render(propsName))

    // The reason to have `Some[String]` as a return type here is
    // https://github.com/scala/bug/issues/12232
    // So sad :(
    def unapply(version: Version): Some[String] =
      Some(version.value)

  }

  opaque type ScalaVersion = String
  object ScalaVersion {
    def apply(scalaVersion: String): ScalaVersion = scalaVersion

    extension (scalaVersion: ScalaVersion) {
      def value: String = scalaVersion

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, scalaVersion.value)
    }

    given eq: Eq[ScalaVersion] = Eq.fromUniversalEquals[ScalaVersion]

    given show: Show[ScalaVersion] = _.value

    given named: Named[ScalaVersion] = Named.named("scalaVersion")

    given scalaVersionRender: Render[ScalaVersion] =
      Render.namedRender(
        "scalaVersion",
        (propsName, scalaVersion) => scalaVersion.render(propsName)
      )

    def unapply(scalaVersion: ScalaVersion): Option[String] =
      Option(scalaVersion.value)

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

  extension (s: RenderedString) {
    def toQuotedString: String = StringUtils.quoteRenderedString(s)

    def innerValue: String = s match {
      case RenderedString.WithProps(s)        => s
      case RenderedString.WithoutProps(s)     => s
      case RenderedString.NoQuotesRequired(s) => s
    }
  }

  extension (stringList: Seq[String]) {
    def stringsMkString: String =
      stringList.mkString

    def stringsMkString(sep: String): String =
      stringList.mkString(sep)

    def stringsMkString(start: String, sep: String, end: String): String =
      stringList.mkString(start, sep, end)
  }

}
