package maven2sbt.core

import StringUtils.*
import cats.*
import cats.syntax.show.*

import maven2sbt.core.data.*

/** @author Kevin Lee
  * @since 2019-04-22
  */
sealed trait Exclusion

object Exclusion {
  final case class Scala(groupId: GroupId, artifactId: ArtifactId) extends Exclusion
  final case class Java(groupId: GroupId, artifactId: ArtifactId) extends Exclusion

  def scala(groupId: GroupId, artifactId: ArtifactId): Exclusion = Scala(groupId, artifactId)
  def java(groupId: GroupId, artifactId: ArtifactId): Exclusion  = Java(groupId, artifactId)

  implicit class ExclusionOps(private val exclusion: Exclusion) extends AnyVal {
    def groupId: GroupId = exclusion match {
      case Exclusion.Scala(groupId, _) => groupId
      case Exclusion.Java(groupId, _) => groupId
    }

    def artifactId: ArtifactId = exclusion match {
      case Exclusion.Scala(_, artifactId) => artifactId
      case Exclusion.Java(_, artifactId) => artifactId
    }
  }

  implicit val exclusionEq: Eq[Exclusion]     = Eq.fromUniversalEquals[Exclusion]
  implicit val exclusionShow: Show[Exclusion] = excl =>
    s"Exclusion(groupId: ${excl.groupId.show}, artifactId: ${excl.artifactId.show})"

  implicit val exclusionRender: Render[Exclusion] =
    Render.namedRender("exclusion", Exclusion.renderExclusionRule)

  implicit val exclusionsRender: Render[List[Exclusion]] =
    Render.namedRender("exclusion", Exclusion.renderExclusions)

  def renderExclusionRule(propsName: Props.PropsName, exclusion: Exclusion): RenderedString = exclusion match {
    case Exclusion.Scala(groupId, artifactId) =>
      val groupIdStr    = StringUtils.renderWithProps(propsName, groupId.value).toQuotedString
      val artifactIdStr = StringUtils.renderWithProps(propsName, artifactId.value).toQuotedString
      RenderedString.noQuotesRequired(
        s"""$groupIdStr %% $artifactIdStr""",
      )
    case Exclusion.Java(groupId, artifactId) =>
      val groupIdStr    = StringUtils.renderWithProps(propsName, groupId.value).toQuotedString
      val artifactIdStr = StringUtils.renderWithProps(propsName, artifactId.value).toQuotedString
      RenderedString.noQuotesRequired(
        s"""$groupIdStr % $artifactIdStr""",
      )
  }

  def renderExclusions(propsName: Props.PropsName, exclusions: List[Exclusion]): RenderedString = exclusions match {
    case Nil =>
      RenderedString.noQuotesRequired("")
    case exclusion :: Nil =>
      import maven2sbt.core.syntax.render.*
      RenderedString.noQuotesRequired(
        s""" excludeAll(${exclusion.render(propsName).toQuotedString})""",
      )
    case x :: xs =>
      val idt = indent(8)
      import maven2sbt.core.syntax.render.*
      RenderedString.noQuotesRequired(
        s""" excludeAll(
           |$idt  ${x.render(propsName).toQuotedString},
           |$idt  ${xs.map(_.render(propsName).toQuotedString).stringsMkString(s",\n$idt  ")}
           |$idt)""".stripMargin,
      )
  }
}
