package maven2sbt.core

import StringUtils._
import cats._
import cats.syntax.show._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  implicit val eq: Eq[Exclusion] = Eq.fromUniversalEquals[Exclusion]
  implicit val show: Show[Exclusion] = excl => s"Exclusion(groupId: ${excl.groupId.show}, artifactId: ${excl.artifactId.show})"

  implicit final val exclusionRender: Render[Exclusion] =
    Render.namedRender("exclusion", Exclusion.renderExclusionRule)

  implicit final val exclusionsRender: Render[List[Exclusion]] =
    Render.namedRender("exclusion", Exclusion.renderExclusions)

  def renderExclusionRule(propsName: Props.PropsName, exclusion: Exclusion): RenderedString = exclusion match {
    case Exclusion(groupId, artifactId) =>
      val groupIdStr = StringUtils.renderWithProps(propsName, groupId.groupId).toQuotedString
      val artifactIdStr = StringUtils.renderWithProps(propsName, artifactId.artifactId).toQuotedString
      RenderedString.noQuotesRequired(
        s"""ExclusionRule(organization = $groupIdStr, name = $artifactIdStr)"""
      )
  }

  def renderExclusions(propsName: Props.PropsName, exclusions: List[Exclusion]): RenderedString = exclusions match {
    case Nil =>
      RenderedString.noQuotesRequired("")
    case Exclusion(groupId, artifactId) :: Nil =>
      RenderedString.noQuotesRequired(
        s""" exclude(${StringUtils.renderWithProps(propsName, groupId.groupId).toQuotedString}, """ +
          s"""${StringUtils.renderWithProps(propsName, artifactId.artifactId).toQuotedString})"""
      )
    case x :: xs =>
      val idt = indent(8)
      RenderedString.noQuotesRequired(
        s""" excludeAll(
           |$idt  ${Render[Exclusion].render(propsName, x).toQuotedString},
           |$idt  ${xs.map(Render[Exclusion].render(propsName, _).toQuotedString).stringsMkString(s",\n$idt  ")}
           |$idt)""".stripMargin
      )
  }
}