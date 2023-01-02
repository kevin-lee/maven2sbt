package maven2sbt.core

import cats.Show

import scala.language.postfixOps
import scala.xml.Elem

/** @author Kevin Lee
  * @since 2019-04-21
  */
final case class ProjectInfo(
  groupId: GroupId,
  artifactId: ArtifactId,
  version: Version,
)

object ProjectInfo {

  def from(pom: Elem): ProjectInfo = {
    val groupIdStr = (pom \ "groupId").text
    val groupId    =
      if (groupIdStr.isBlank)
        GroupId((pom \ "parent" \ "groupId").text)
      else
        GroupId(groupIdStr)
    val artifactId = ArtifactId(pom \ "artifactId" text)
    val version    = Version(pom \ "version" text)
    ProjectInfo(groupId, artifactId, version)
  }

  implicit val projectInfoShow: Show[ProjectInfo] =
    projectInfo =>
      s"ProjectInfo(groupId: ${projectInfo.groupId.value}, " +
        s"artifactId: ${projectInfo.artifactId.value}, " +
        s"version: ${projectInfo.version.value})"
}
