package maven2sbt.core

import Common._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  def renderExclusionRule(exclusion: Exclusion): String = exclusion match {
    case Exclusion(groupId, artifactId) =>
      val groupIdStr = MavenProperty.toPropertyNameOrItself(groupId.groupId)
      val artifactIdStr = MavenProperty.toPropertyNameOrItself(artifactId.artifactId)
      s"""ExclusionRule(organization = $groupIdStr, artifact = $artifactIdStr)"""
  }

  def renderExclusions(exclusions: Seq[Exclusion]): String = exclusions match {
    case Nil =>
      ""
    case Exclusion(groupId, artifactId) :: Nil =>
      s""" exclude("${groupId.groupId}", "${artifactId.artifactId}")"""
    case x :: xs =>
      val idt = indent(8)
      s""" excludeAll(
         |$idt  ${renderExclusionRule(x)},
         |$idt  ${xs.map(renderExclusionRule).mkString(s",\n$idt  ")}
         |$idt)""".stripMargin
  }
}