package kevinlee.maven2sbt

import hedgehog._
import hedgehog.runner._

import scala.xml._

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object ProjectInfoSpec extends Properties {
  def generatePom(groupId: GroupId, artifactId: ArtifactId, version: Version): Elem =
    <project>
      <groupId>{groupId.groupId}</groupId>
      <artifactId>{artifactId.artifactId}</artifactId>
      <version>{version.version}</version>
    </project>

  override def tests: List[Test] = List(
    property("test from", testFrom)
  )

  def testFrom: Property = for {
    groupId <- Gens.genGroupId.log("groupId")
    artifactId <- Gens.genArtifactId.log("artifactId")
    version <- Gens.genVersion.log("version")
  } yield {
    val pom = generatePom(groupId, artifactId, version)
    val expected = ProjectInfo(groupId, artifactId, version)
    val actual = ProjectInfo.from(pom)
    actual ==== expected
  }

}
