package maven2sbt.core

import hedgehog.*
import hedgehog.runner.*
import maven2sbt.core.Props.PropsName

/** @author Kevin Lee
  * @since 2019-04-21
  */
object ScopeSpec extends Properties {

  private val scopeAndRendered = List(
    (Scope.compile, "Compile"),
    (Scope.test, "Test"),
    (Scope.provided, "Provided"),
    (Scope.runtime, "Runtime"),
    (Scope.system, "sbt.Configurations.System"),
    (Scope.default, ""),
    (Scope.unknown("something else"), "something else"),
    (Scope.unknown(raw"$${something.else}"), raw"$${something.else}"),
  )

  private val scopeAndRenderedNonCompileWithPrefix = List(
    (Scope.compile, "Compile"),
    (Scope.test, "Test"),
    (Scope.provided, "Provided"),
    (Scope.runtime, "Runtime"),
    (Scope.system, "sbt.Configurations.System"),
    (Scope.default, ""),
    (Scope.unknown("something else"), "\"something else\""),
    (Scope.unknown(raw"$${something.else}"), "props.somethingElse"),
  )

  private val mavenScopeAndScope = List(
    ("compile", Scope.compile),
    ("test", Scope.test),
    ("provided", Scope.provided),
    ("runtime", Scope.runtime),
    ("system", Scope.system),
    ("", Scope.default),
    ("something else", Scope.unknown("something else")),
  )

  override def tests: List[Test] = List(
    example("test scope render", testRender),
    property("test scope renderWithPrefix", testRenderWithPrefix),
    example("test scope parse", testParseValid),
  )

  def testRender: Result = Result.all(for {
    (scope, expected) <- scopeAndRendered
  } yield Scope.render(scope) ==== expected)

  def testRenderWithPrefix: Property = for {
    prefix <- Gen.string(Gen.unicode, Range.linear(0, 20)).log("prefix")
  } yield {
    Result.all(
      scopeAndRenderedNonCompileWithPrefix.map {
        case (scope, expected) =>
          Scope.renderNonCompileWithPrefix(prefix, scope, PropsName("props")) ==== (scope match {
            case Scope.Default | Scope.Compile =>
              ""

            case Scope.Unknown(_) =>
              s"$prefix$expected"

            case Scope.Test | Scope.Provided | Scope.Runtime | Scope.System =>
              s"$prefix$expected"
          })
      },
    )
  }

  // TODO: Add more tests for scope rendering

  def testParseValid: Result =
    Result.all(
      mavenScopeAndScope.map {
        case (mavenScope, scope) =>
          Scope.parse(mavenScope) ==== scope
      },
    )

}
