package kevinlee.maven2sbt

import scala.language.postfixOps
import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class ProjectInfo(
    groupId: GroupId
  , artifactId: ArtifactId
  , version: Version
  )

object ProjectInfo {

  def from(pom: Elem): ProjectInfo = {
    val groupId = GroupId(pom \ "groupId" text)
    val artifactId = ArtifactId(pom \ "artifactId" text)
    val version = Version(pom \ "version" text)
    ProjectInfo(groupId, artifactId, version)
  }

}
