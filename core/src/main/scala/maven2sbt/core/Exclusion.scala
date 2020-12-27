package maven2sbt.core

import M2SStringOps._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  def renderExclusionRule(propsName: Props.PropsName, exclusion: Exclusion): String = exclusion match {
    case Exclusion(groupId, artifactId) =>
      val groupIdStr = M2SStringOps.toPropertyNameOrItself(propsName, groupId.groupId)
      val artifactIdStr = M2SStringOps.toPropertyNameOrItself(propsName, artifactId.artifactId)
      s"""ExclusionRule(organization = $groupIdStr, name = $artifactIdStr)"""
  }

  def renderExclusions(propsName: Props.PropsName, exclusions: List[Exclusion]): String = exclusions match {
    case Nil =>
      ""
    case Exclusion(groupId, artifactId) :: Nil =>
      s""" exclude(${M2SStringOps.toPropertyNameOrItself(propsName, groupId.groupId)}, """ +
        s"""${M2SStringOps.toPropertyNameOrItself(propsName, artifactId.artifactId)})"""
    case x :: xs =>
      val idt = indent(8)
      s""" excludeAll(
         |$idt  ${renderExclusionRule(propsName, x)},
         |$idt  ${xs.map(renderExclusionRule(propsName, _)).mkString(s",\n$idt  ")}
         |$idt)""".stripMargin
  }
}