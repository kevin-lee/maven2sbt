package maven2sbt.core

import cats.syntax.all._

import hedgehog._
import hedgehog.runner._

import maven2sbt.core.Gens.ExpectedMavenProperty

/**
 * @author Kevin Lee
 * @since 2020-09-05
 */
object BuildSbtSpec extends Properties {
  override def tests: List[Prop] = List(
    property("test BuildSbt.Prop.fromMavenProperty", PropSpec.testFromMavenProperty),
    property(
      "[Render][Repository] test BuildSbt.renderListOfFieldValue(None, List.empty[Repository], n)",
      RenderRepositorySpec.testRenderToResolvers0
    ),
    property(
      "[Render][Repository] test BuildSbt.renderListOfFieldValue(None, List(repository), n)",
      RenderRepositorySpec.testRenderToResolvers1
    ),
    property(
      "[Render][Repository] test BuildSbt.renderListOfFieldValue(None, List(repository1, repository2, ...), n)",
      RenderRepositorySpec.testRenderToResolversMany
    ),
    property(
      "[Render][Dependency] test BuildSbt.renderListOfFieldValue(None, List.empty[Dependency], n)",
      RenderDependencySpec.testRenderLibraryDependenciesEmpty
    ),
    property(
      "[Render][Dependency] test BuildSbt.renderListOfFieldValue(None, List(dependency), n)",
      RenderDependencySpec.testRenderLibraryDependencies1
    ),
    property(
      "[Render][Dependency] test BuildSbt.renderListOfFieldValue(None, List(dependency1, dependency2, ...), n)",
      RenderDependencySpec.testRenderLibraryDependenciesMany
    )
  )

  object PropSpec {

    def testFromMavenProperty: Property = for {
      mavenProperties <- Gens.genMavenPropertyWithExpectedRendered.list(Range.linear(0, 10))
          .log("mavenProperties")
    } yield {
      val (expected, actual) = mavenProperties.map {
        case (ExpectedMavenProperty(expectedMavenProperty), mavenProperty) =>
          (
            BuildSbt.Prop(
                BuildSbt.PropName(expectedMavenProperty.key),
                BuildSbt.PropValue(expectedMavenProperty.value)
              ),
            BuildSbt.Prop.fromMavenProperty(mavenProperty)
          )
      }.unzip
      actual ==== expected
    }

  }

  object RenderRepositorySpec {

    def testRenderToResolvers0: Property = for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield {
      val expected = none[String]
      val actual = BuildSbt.renderListOfFieldValue(none[String], List.empty[Repository], n)
      actual ==== expected
    }

    def testRenderToResolvers1: Property = for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
      repository <- Gens.genRepository.log("repository")
    } yield {
      val expected = s"""resolvers += "${repository.name.fold(repository.id.repoId)(_.repoName)}" at "${repository.url.repoUrl}"""".some
      val actual = BuildSbt.renderListOfFieldValue(none[String], List(repository), n)
      actual ==== expected
    }

    def testRenderToResolversMany: Property = for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
      repositories <- Gens.genRepository.list(Range.linear(2, 10)).log("repositories")
    } yield {
      val idt = " " * n
      val expected =
        s"""resolvers ++= List(
           |$idt${repositories.map(Repository.render).mkString("  ", s",\n$idt  ", "")}
           |$idt)""".stripMargin.some
      val actual = BuildSbt.renderListOfFieldValue(none[String], repositories, n)
      actual ==== expected
    }
  }

  object RenderDependencySpec {

    def testRenderLibraryDependenciesEmpty: Property = for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
    } yield {
      val expected = none[String]
      val actual = BuildSbt.renderListOfFieldValue(none[String], List.empty[Dependency], n)
      actual ==== expected
    }

    def testRenderLibraryDependencies1: Property = for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
      dependency <- Gens.genDependency.log("dependency")
    } yield {
      val expected = s"libraryDependencies += ${Dependency.render(dependency)}".some
      val actual = BuildSbt.renderListOfFieldValue(none[String], List(dependency), n)
      actual ==== expected
    }

    def testRenderLibraryDependenciesMany: Property = for {
      n <- Gen.int(Range.linear(0, 10)).log("n")
      libraryDependencies <- Gens.genDependency.list(Range.linear(2, 10)).log("libraryDependencies")
    } yield {
      val idt = " " * n
      val expected =
        s"""libraryDependencies ++= List(
           |$idt${libraryDependencies.map(Dependency.render).mkString("  ", s",\n$idt  ", "")}
           |$idt)""".stripMargin.some
      val actual = BuildSbt.renderListOfFieldValue(none[String], libraryDependencies, n)
      actual ==== expected
    }

  }

}
