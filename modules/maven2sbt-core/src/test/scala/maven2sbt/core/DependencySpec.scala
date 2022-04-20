package maven2sbt.core

import cats.syntax.all._
import hedgehog._
import hedgehog.runner._

import scala.xml.Elem

/** @author Kevin Lee
  * @since 2019-04-22
  */
object DependencySpec extends Properties {

  override def tests: List[Test] = List(
    property("test from", testFrom),
    property("test render", testRender),
    property("test render with Libs", testRenderWithLibs),
    property("test render with Libs with different version", testRenderWithLibsWithVersionDiff),
    property("test render with Libs with different scope", testRenderWithLibsWithScopeDiff),
    property("test render with Libs with different exclusions", testRenderWithLibsWithExclusionDiff),
    property(
      "test render with Libs with different exclusions (lib exclusions are empty)",
      testRenderWithLibsWithExclusionDiffAndEmptyLibExclusions
    ),
    property("test render with Libs with different scope and exclusions", testRenderWithLibsWithScopeAndExclusionsDiff)
  )

  def testFrom: Property = for {
    dependencies <- Gens.genDependency.list(Range.linear(0, 10)).log("dependencies")
  } yield {
    val scalaBinaryVersionName = ScalaBinaryVersion.Name("scala.binary.Version")
    val actual = Dependency.from(generatePom(dependencies, scalaBinaryVersionName), scalaBinaryVersionName.some)
    actual ==== dependencies
  }

  def testRender: Property = for {
    libsName   <- Gens.genLibsName.log("libsName")
    dependency <- Gens.genDependency.log("dependency")
  } yield {
    val propsName = Props.PropsName("props")
    val libs      = Libs(List.empty[(Libs.LibValName, Dependency)])
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val expected                                                                        =
      RenderedString.noQuotesRequired(
        s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%"} "$artifactId" % "$version"${Scope
            .renderNonCompileWithPrefix(" % ", scope)}${Exclusion
            .renderExclusions(propsName, exclusions)
            .toQuotedString}"""
      )
    val actual = Dependency.render(propsName, libsName, libs, dependency)
    actual ==== expected
  }

  def testRenderWithLibs: Property = for {
    libsName   <- Gens.genLibsName.log("libsName")
    dependency <- Gens.genDependency.log("dependency")
  } yield {
    val propsName = Props.PropsName("props")
    val libs      = Libs(List((Libs.LibValName("myLib"), dependency)))
    val expected  =
      RenderedString.noQuotesRequired(s"${libsName.value}.myLib")
    val actual    = Dependency.render(propsName, libsName, libs, dependency)
    actual ==== expected
  }

  def testRenderWithLibsWithVersionDiff: Property = for {
    libsName   <- Gens.genLibsName.log("libsName")
    dependency <- Gens.genDependency.log("dependency")
  } yield {
    val propsName                                                                       = Props.PropsName("props")
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val libs                                                                            = Libs(
      List(
        (
          Libs.LibValName("myLib"),
          (dependency match {
            case dep @ Dependency.Java(_, _, version, _, _) =>
              dep.copy(version = Version(version.value + "-alpha"))
            case dep @ Dependency.Scala(_, _, version, _, _) =>
              dep.copy(version = Version(version.value + "-alpha"))
          })
        )
      )
    )
    val expected                                                                        =
      RenderedString.noQuotesRequired(
        s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%"} "$artifactId" % "$version"${Scope
            .renderNonCompileWithPrefix(" % ", scope)}${Exclusion
            .renderExclusions(propsName, exclusions)
            .toQuotedString}"""
      )
    val actual = Dependency.render(propsName, libsName, libs, dependency)
    actual ==== expected
  }

  def testRenderWithLibsWithScopeDiff: Property = for {
    libsName   <- Gens.genLibsName.log("libsName")
    dependency <- Gens.genDependency.log("dependency")
  } yield {

    val propsName                                                                       = Props.PropsName("props")
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val myLib: Dependency                                                               = (dependency match {
      case dep @ Dependency.Java(_, _, _, scope, _) =>
        dep.copy(scope = differentScope(scope))
      case dep @ Dependency.Scala(_, _, _, scope, _) =>
        dep.copy(scope = differentScope(scope))
    })
    val libs     = Libs(List((Libs.LibValName("myLib"), myLib)))
    val expected = RenderedString.noQuotesRequired(
      if (myLib.scope === Scope.compile || myLib.scope === Scope.default) {
        s"""${libsName.value}.myLib${Scope.renderNonCompileWithPrefix(" % ", scope)}${Exclusion
            .renderExclusions(propsName, myLib.exclusions.diff(exclusions))
            .toQuotedString}"""
      } else {
        s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%"} "$artifactId" % "$version"${Scope
            .renderNonCompileWithPrefix(" % ", scope)}${Exclusion
            .renderExclusions(propsName, exclusions)
            .toQuotedString}"""
      }
    )
    val actual   = Dependency.render(propsName, libsName, libs, dependency)
    (actual ==== expected).log(s"""libs: ${libs.show}
         |dependency: ${dependency.show}
         |""".stripMargin)
  }

  def testRenderWithLibsWithExclusionDiff: Property = for {
    libsName      <- Gens.genLibsName.log("libsName")
    dependency    <- Gens.genDependency.log("dependency")
    libExclusions <- Gens.genExclusion.list(Range.linear(1, 5)).log("libExclusions")
  } yield {

    val propsName         = Props.PropsName("props")
    val myLib: Dependency = dependency match {
      case dep @ Dependency.Java(_, _, _, _, _) =>
        dep.copy(exclusions = libExclusions)
      case dep @ Dependency.Scala(_, _, _, _, _) =>
        dep.copy(exclusions = libExclusions)
    }
    val libs              = Libs(List((Libs.LibValName("myLib"), myLib)))
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val expected                                                                        =
      RenderedString.noQuotesRequired(
        s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%"} "$artifactId" % "$version"${Scope
            .renderNonCompileWithPrefix(" % ", scope)}${Exclusion
            .renderExclusions(propsName, exclusions)
            .toQuotedString}"""
      )

    val actual = Dependency.render(propsName, libsName, libs, dependency)
    (actual ==== expected).log(s"""libs: ${libs.show}
         |dependency: ${dependency.show}
         |""".stripMargin)
  }

  def testRenderWithLibsWithExclusionDiffAndEmptyLibExclusions: Property = for {
    libsName   <- Gens.genLibsName.log("libsName")
    dependency <- Gens.genDependencyWithNonEmptyExclusions.log("dependency")
  } yield {

    val propsName         = Props.PropsName("props")
    val myLib: Dependency = dependency match {
      case dep @ Dependency.Java(_, _, _, _, _) =>
        dep.copy(exclusions = List.empty[Exclusion])
      case dep @ Dependency.Scala(_, _, _, _, _) =>
        dep.copy(exclusions = List.empty[Exclusion])
    }
    val libs              = Libs(List((Libs.LibValName("myLib"), myLib)))
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val expected                                                                        =
      RenderedString.noQuotesRequired(
        s"""${libsName.value}.myLib${Exclusion.renderExclusions(propsName, exclusions).toQuotedString}"""
      )

    val actual = Dependency.render(propsName, libsName, libs, dependency)
    (actual ==== expected).log(s"""libs: ${libs.show}
         |dependency: ${dependency.show}
         |""".stripMargin)
  }

  def testRenderWithLibsWithScopeAndExclusionsDiff: Property = for {
    libsName      <- Gens.genLibsName.log("libsName")
    dependency    <- Gens.genDependency.log("dependency")
    libExclusions <- Gens.genExclusion.list(Range.linear(1, 5)).log("libExclusions")
  } yield {

    val propsName                                                                       = Props.PropsName("props")
    val (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) = dependency.tupled
    val myLib: Dependency                                                               = (dependency match {
      case dep @ Dependency.Java(_, _, _, scope, _) =>
        dep.copy(scope = differentScope(scope), exclusions = libExclusions)
      case dep @ Dependency.Scala(_, _, _, scope, _) =>
        dep.copy(scope = differentScope(scope), exclusions = libExclusions)
    })
    val libs     = Libs(List((Libs.LibValName("myLib"), myLib)))
    val expected = RenderedString.noQuotesRequired(
      s""""$groupId" ${if (dependency.isScalaLib) "%%" else "%"} "$artifactId" % "$version"${Scope
          .renderNonCompileWithPrefix(" % ", scope)}${Exclusion
          .renderExclusions(propsName, exclusions)
          .toQuotedString}"""
    )
    val actual   = Dependency.render(propsName, libsName, libs, dependency)
    (actual ==== expected).log(s"""libs: ${libs.show}
         |dependency: ${dependency.show}
         |""".stripMargin)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def generatePom(dependencies: List[Dependency], scalaBinaryVersionName: ScalaBinaryVersion.Name): Elem =
    <project>
      <dependencies>
        {
      dependencies.map { dependency =>
        dependency.tupled match {
          case (GroupId(groupId), ArtifactId(artifactId), Version(version), scope, exclusions) =>
            val mavenScope    = Scope.renderToMaven(scope)
            val artifactIdVal = if (dependency.isScalaLib) {
              s"${artifactId}_$${${scalaBinaryVersionName.value}}"
            } else {
              artifactId
            }
            val excls         = exclusions.map {
              case Exclusion(GroupId(groupId), ArtifactId(artifactId)) =>
                <exclusions>
                    <exclusion>
                      <groupId>{groupId}</groupId>
                      <artifactId>{artifactId}</artifactId>
                    </exclusion>
                  </exclusions>
            }
            val dep           =
              <dependency>
                  <groupId>{groupId}</groupId>
                  <artifactId>{artifactIdVal}</artifactId>
                  <version>{version}</version>
                  {excls}
                </dependency>

            if (mavenScope.isEmpty)
              dep
            else
              dep.copy(child = dep.child :+ <scope>{mavenScope}</scope>)
        }
      }
    }
      </dependencies>
    </project>

  private def differentScope(scope: Scope): Scope =
    scala
      .util
      .Random
      .shuffle(Scope.all.diff(List(scope)))
      .headOption
      .getOrElse(sys.error("If you see this, there's something wrong in this test."))

}
