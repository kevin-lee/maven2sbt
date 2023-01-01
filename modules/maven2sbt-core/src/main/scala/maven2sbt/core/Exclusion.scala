package maven2sbt.core

import StringUtils.*
import cats.*
import cats.syntax.show.*

/** @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  implicit val eq: Eq[Exclusion]     = Eq.fromUniversalEquals[Exclusion]
  implicit val exclusionShow: Show[Exclusion] = excl =>
    s"Exclusion(groupId: ${excl.groupId.show}, artifactId: ${excl.artifactId.show})"

  implicit val exclusionRender: Render[Exclusion] =
    Render.namedRender("exclusion", Exclusion.renderExclusionRule)

  implicit val exclusionsRender: Render[List[Exclusion]] =
    Render.namedRender("exclusion", Exclusion.renderExclusions)

  def renderExclusionRule(propsName: Props.PropsName, exclusion: Exclusion): RenderedString = exclusion match {
    case Exclusion(groupId, artifactId) =>
      val groupIdStr    = StringUtils.renderWithProps(propsName, groupId.value).toQuotedString
      val artifactIdStr = StringUtils.renderWithProps(propsName, artifactId.value).toQuotedString
      RenderedString.noQuotesRequired(
        s"""ExclusionRule(organization = $groupIdStr, name = $artifactIdStr)"""
      )
  }

  def renderExclusions(propsName: Props.PropsName, exclusions: List[Exclusion]): RenderedString = exclusions match {
    case Nil =>
      RenderedString.noQuotesRequired("")
    case Exclusion(groupId, artifactId) :: Nil =>
      RenderedString.noQuotesRequired(
        s""" exclude(${StringUtils.renderWithProps(propsName, groupId.value).toQuotedString}, """ +
          s"""${StringUtils.renderWithProps(propsName, artifactId.value).toQuotedString})"""
      )
    case x :: xs =>
      val idt = indent(8)
      import maven2sbt.core.syntax.render.*
      RenderedString.noQuotesRequired(
        s""" excludeAll(
           |$idt  ${x.render(propsName).toQuotedString},
           |$idt  ${xs.map(_.render(propsName).toQuotedString).stringsMkString(s",\n$idt  ")}
           |$idt)""".stripMargin
      )
  }
}
