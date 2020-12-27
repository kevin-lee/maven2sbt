package maven2sbt.core

import Common._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  def renderExclusionRule(propsName: Props.PropsName, exclusion: Exclusion): String = exclusion match {
    case Exclusion(groupId, artifactId) =>
      val groupIdStr = Common.toPropertyNameOrItself(propsName, groupId.groupId)
      val artifactIdStr = Common.toPropertyNameOrItself(propsName, artifactId.artifactId)
      s"""ExclusionRule(organization = $groupIdStr, name = $artifactIdStr)"""
  }

  def renderExclusions(propsName: Props.PropsName, exclusions: List[Exclusion]): String = exclusions match {
    case Nil =>
      ""
    case Exclusion(groupId, artifactId) :: Nil =>
      s""" exclude(${Common.toPropertyNameOrItself(propsName, groupId.groupId)}, """ +
        s"""${Common.toPropertyNameOrItself(propsName, artifactId.artifactId)})"""
    case x :: xs =>
      val idt = indent(8)
      s""" excludeAll(
         |$idt  ${renderExclusionRule(propsName, x)},
         |$idt  ${xs.map(renderExclusionRule(propsName, _)).mkString(s",\n$idt  ")}
         |$idt)""".stripMargin
  }
}