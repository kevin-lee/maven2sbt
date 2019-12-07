package maven2sbt.core

import Common._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
final case class Exclusion(groupId: GroupId, artifactId: ArtifactId)

object Exclusion {

  def renderExclusionRule(exclusion: Exclusion): String = exclusion match {
    case Exclusion(GroupId(groupId), ArtifactId(artifactId)) =>
      val groupIdStr = MavenProperty.toPropertyNameOrItself(groupId)
      val artifactIdStr = MavenProperty.toPropertyNameOrItself(artifactId)
      s"""ExclusionRule(organization = "$groupIdStr", artifact = "$artifactIdStr")"""
  }

  def renderExclusions(exclusions: Seq[Exclusion]): String = exclusions match {
    case Nil =>
      ""
    case Exclusion(GroupId(groupId), ArtifactId(artifactId)) :: Nil =>
      s""" exclude("$groupId", "$artifactId")"""
    case x :: xs =>
      val idt = indent(6)
      s""" excludeAll(
         |$idt${exclusions.map(renderExclusionRule).mkString("  ", s"\n$idt, ", "")}
         |$idt)""".stripMargin
  }
}