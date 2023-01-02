package maven2sbt.core

import hedgehog.*
import hedgehog.runner.*

import scala.xml.*

/** @author Kevin Lee
  * @since 2019-04-22
  */
object ProjectInfoSpec extends Properties {

  override def tests: List[Test] = List(
    property("test from", testFrom),
    property("test from with <parent> elem and it may have the missing groupId in pom.xml", testFromWithParent),
  )

  def testFrom: Property = for {
    groupId    <- Gens.genGroupId.log("groupId")
    artifactId <- Gens.genArtifactId.log("artifactId")
    version    <- Gens.genVersion.log("version")
  } yield {
    val pom      = generatePom(groupId, artifactId, version)
    val expected = ProjectInfo(groupId, artifactId, version)
    val actual   = ProjectInfo.from(pom)
    actual ==== expected
  }

  def testFromWithParent: Property = for {
    parentGroupId <- Gens.genGroupId.log("groupId")
    groupId       <- Gens.genGroupId.option.log("groupId")
    artifactId    <- Gens.genArtifactId.log("artifactId")
    version       <- Gens.genVersion.log("version")
  } yield {
    val pom      = generatePomWithParent(parentGroupId, groupId, artifactId, version)
    val expected =
      ProjectInfo(groupId.fold(parentGroupId)(g => if (g.value.isBlank) parentGroupId else g), artifactId, version)
    val actual   = ProjectInfo.from(pom)
    actual ==== expected
  }

  def generatePom(groupId: GroupId, artifactId: ArtifactId, version: Version): Elem =
    <project>
      <groupId>{groupId.value}</groupId>
      <artifactId>{artifactId.value}</artifactId>
      <version>{version.value}</version>
    </project>

  def generatePomWithParent(
    parentGroupId: GroupId,
    groupId: Option[GroupId],
    artifactId: ArtifactId,
    version: Version,
  ): Elem = {
    <project>
      <parent>
        <groupId>{parentGroupId.value}</groupId>
      </parent>
      <groupId>{groupId.fold("")(_.value)}</groupId>
      <artifactId>{artifactId.value}</artifactId>
      <version>{version.value}</version>
    </project>
  }

}
