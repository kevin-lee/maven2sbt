package maven2sbt.core

import StringUtils._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  def renderExclusionRule(propsName: Props.PropsName, exclusion: Exclusion): String = exclusion match {
    case Exclusion(groupId, artifactId) =>
      val groupIdStr = StringUtils.renderWithProps(propsName, groupId.groupId).toQuotedString
      val artifactIdStr = StringUtils.renderWithProps(propsName, artifactId.artifactId).toQuotedString
      s"""ExclusionRule(organization = $groupIdStr, name = $artifactIdStr)"""
  }

  def renderExclusions(propsName: Props.PropsName, exclusions: List[Exclusion]): String = exclusions match {
    case Nil =>
      ""
    case Exclusion(groupId, artifactId) :: Nil =>
      s""" exclude(${StringUtils.renderWithProps(propsName, groupId.groupId).toQuotedString}, """ +
        s"""${StringUtils.renderWithProps(propsName, artifactId.artifactId).toQuotedString})"""
    case x :: xs =>
      val idt = indent(8)
      s""" excludeAll(
         |$idt  ${renderExclusionRule(propsName, x)},
         |$idt  ${xs.map(renderExclusionRule(propsName, _)).mkString(s",\n$idt  ")}
         |$idt)""".stripMargin
  }
}