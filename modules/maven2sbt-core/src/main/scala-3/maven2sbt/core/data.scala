package maven2sbt.core

import just.fp.Named

import cats.*

import refined4s.*
import refined4s.modules.cats.derivation.*

/** @author Kevin Lee
  * @since 2019-04-22
  */
object data {

  type GroupId = GroupId.Type
  object GroupId extends Newtype[String], CatsEqShow[String] {

    given named: Named[GroupId] = Named.named("organization")

    given groupIdRender: Render[GroupId] =
      Render.namedRender("groupId", (propsName, groupId) => StringUtils.renderWithProps(propsName, groupId.value))
  }

  type ArtifactId = ArtifactId.Type
  object ArtifactId extends Newtype[String], CatsEqShow[String] {

    extension (artifactId: ArtifactId) {

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, artifactId.value)

    }

    given named: Named[ArtifactId] = Named.named("name")

    given artifactIdRender: Render[ArtifactId] =
      Render.namedRender(
        "artifactId",
        (propsName, artifactId) => artifactId.render(propsName),
      )

  }

  type Version = Version.Type
  object Version extends Newtype[String], CatsEqShow[String] {

    extension (version: Version) {

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, version.value)
    }

    given named: Named[Version] = Named.named("version")

    given versionRender: Render[Version] =
      Render.namedRender("version", (propsName, version) => version.render(propsName))

  }

  type ScalaVersion = ScalaVersion.Type
  object ScalaVersion extends Newtype[String], CatsEqShow[String] {

    extension (scalaVersion: ScalaVersion) {

      def render(propsName: Props.PropsName): RenderedString =
        StringUtils.renderWithProps(propsName, scalaVersion.value)
    }

    given named: Named[ScalaVersion] = Named.named("scalaVersion")

    given scalaVersionRender: Render[ScalaVersion] =
      Render.namedRender(
        "scalaVersion",
        (propsName, scalaVersion) => scalaVersion.render(propsName),
      )

  }

  type ScalaBinaryVersion = ScalaBinaryVersion.Type
  object ScalaBinaryVersion extends Newtype[String], CatsEqShow[String] {

    type Name = Name.Type
    object Name extends Newtype[String], CatsEqShow[String]
  }

  extension (s: RenderedString) {
    def toQuotedString: String = StringUtils.quoteRenderedString(s)

    def innerValue: String = s match {
      case RenderedString.WithProps(s) => s
      case RenderedString.WithoutProps(s) => s
      case RenderedString.NoQuotesRequired(s) => s
    }
  }

  extension (stringList: Seq[String]) {
    def stringsMkString: String =
      stringList.mkString

    @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
    def stringsMkString(sep: String): String =
      stringList.mkString(sep)

    @SuppressWarnings(Array("org.wartremover.warts.Overloading"))
    def stringsMkString(start: String, sep: String, end: String): String =
      stringList.mkString(start, sep, end)
  }

}
