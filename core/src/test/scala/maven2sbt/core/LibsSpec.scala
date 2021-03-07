package maven2sbt.core

import cats.syntax.all._
import hedgehog._
import hedgehog.runner._

import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object LibsSpec extends Properties {

  override def tests: List[Test] =
    List(property("test from", testFrom), property("test render", testRender))

  def testFrom: Property =
    for {
      libValNameAndDependencies <- Gens.genLibValNameAndDependency
        .list(Range.linear(0, 10))
        .log("libValNamesAndDependencies")
    } yield {
      val expected = Libs(libValNameAndDependencies)
      val scalaBinaryVersionName = ScalaBinaryVersion.Name("scala.binary.Version")
      val actual = Libs.from(generatePom(libValNameAndDependencies.map {
        case (_, dependency) => dependency
      }, scalaBinaryVersionName), scalaBinaryVersionName.some)
      actual ==== expected
    }

  def testRender: Property =
    for {
      libValNameAndDependencies <- Gens.genLibValNameAndDependency
        .list(Range.linear(0, 10))
        .log("libValNamesAndDependencies")
    } yield {
      val propsName = Props.PropsName("props")
      val libsName = Libs.LibsName("libs")
      val libs = Libs(libValNameAndDependencies)
      val indentSize = 2
      val indent = " " * indentSize

      val libsString = for {
        (libValName, dependency) <- libValNameAndDependencies
        (
          GroupId(groupId),
          ArtifactId(artifactId),
          Version(version),
          scope,
          exclusions
        ) = dependency.tupled
        depString = RenderedString.noQuotesRequired(
          s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%" } "$artifactId" % "$version"${Scope
            .renderNonCompileWithPrefix(" % ", scope)}${Exclusion
            .renderExclusions(propsName, exclusions)
            .toQuotedString}"""
        )
      } yield
        s"${indent}val ${libValName.libValName} = ${depString.toQuotedString}"

      val expected =
        s"""lazy val ${libsName.libsName} = new {
         |${libsString.mkString("\n")}
         |}""".stripMargin

      val actual = Libs.render(propsName, libsName, indentSize, libs)

      actual ==== expected
    }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def generatePom(dependencies: List[Dependency], scalaBinaryVersionName: ScalaBinaryVersion.Name): Elem =
    <project>
      <dependencyManagement>
        <dependencies>
          {
          dependencies.map { dependency =>
            val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
            val artifactIdVal = if (dependency.isScalaLib) {
              s"${artifactId}_$${${scalaBinaryVersionName.name}}"
            } else {
              artifactId
            }
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
        </dependencies>
      </dependencyManagement>
    </project>

}
