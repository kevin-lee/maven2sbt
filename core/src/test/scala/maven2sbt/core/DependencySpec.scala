package maven2sbt.core

import cats.syntax.all._
import hedgehog._
import hedgehog.runner._

import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object DependencySpec extends Properties {

  override def tests: List[Test] = List(
    property("test from", testFrom)
  , property("test render", testRender)
  )

  def testFrom: Property = for {
    dependencies <- Gens.genDependency.list(Range.linear(0, 10)).log("dependencies")
  } yield {
    val scalaBinaryVersionName = ScalaBinaryVersion.Name("scala.binary.Version")
    val actual = Dependency.from(generatePom(dependencies, scalaBinaryVersionName), scalaBinaryVersionName.some)
    actual ==== dependencies
  }

  def testRender: Property = for {
    dependency <- Gens.genDependency.log("dependency")
  } yield {
    val propsName = Props.PropsName("props")
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val expected =
      RenderedString.noQuotesRequired(
        s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%"} "$artifactId" % "$version"${Scope.renderWithPrefix(" % ", scope)}${Exclusion.renderExclusions(propsName, exclusions).toQuotedString}"""
      )
    val actual = Dependency.render(propsName, dependency)
    actual ==== expected
  }


  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def generatePom(dependencies: List[Dependency], scalaBinaryVersionName: ScalaBinaryVersion.Name): Elem =
    <project>
      <dependencies>
        {
        dependencies.map { dependency =>
          dependency.tupled match {
            case (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) =>
              val mavenScope = Scope.renderToMaven(scope)
              val artifactIdVal = if (dependency.isScalaLib) {
                s"${artifactId}_$${${scalaBinaryVersionName.name}}"
              } else {
                artifactId
              }
              val excls = exclusions.map {
                case Exclusion(GroupId(groupId), ArtifactId(artifactId)) =>
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
                  <artifactId>{ artifactIdVal }</artifactId>
                  <version>{ version }</version>
                  { excls }
                </dependency>

              if (mavenScope.isEmpty)
                dep
              else
                dep.copy(child = dep.child :+ <scope>{ mavenScope }</scope>)
          }
        }
        }
      </dependencies>
    </project>

}
