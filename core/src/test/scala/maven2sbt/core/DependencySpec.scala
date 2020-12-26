package maven2sbt.core

import hedgehog._
import hedgehog.runner._

import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object DependencySpec extends Properties {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def generatePom(dependencies: List[Dependency]): Elem =
    <project>
      <dependencies>
        {
          dependencies.map {
            case Dependency(GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) =>
              val mavenScope = Scope.renderToMaven(scope)
              val excls = exclusions.map { case Exclusion(GroupId(groupId), ArtifactId(artifactId)) =>
                <exclusions>
                  <exclusion>
                    <groupId>{ groupId }</groupId>
                    <artifactId>{ artifactId }</artifactId>
                  </exclusion>
                </exclusions>
              }
              val dep =
                <dependency>
                  <groupId>{ groupId }</groupId>
                  <artifactId>{ artifactId }</artifactId>
                  <version>{ version }</version>
                  { excls }
                </dependency>

              if (mavenScope.isEmpty)
                dep
              else
                dep.copy(child = dep.child :+ <scope>{ mavenScope }</scope>)
          }
        }
      </dependencies>
    </project>

  override def tests: List[Test] = List(
    property("test from", testFrom)
  , property("test render", testRender)
  )

  def testFrom: Property = for {
    dependencies <- Gens.genDependency.list(Range.linear(0, 10)).log("dependencies")
  } yield {
    val actual = Dependency.from(generatePom(dependencies))
    actual ==== dependencies
  }

  def testRender: Property = for {
    dependency <- Gens.genDependency.log("dependency")
  } yield {
    val propsName = Props.PropsName("props")
    val Dependency(GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency
    val expected = s""""$groupId" % "$artifactId" % "$version"${Scope.renderWithPrefix(" % ", scope)}${Exclusion.renderExclusions(propsName, exclusions)}"""
    val actual = Dependency.render(dependency)
    actual ==== expected
  }

}
