package maven2sbt.core

import hedgehog._
import hedgehog.runner._

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
object ScopeSpec extends Properties {

  private val scopeAndRendered = List(
    (Scope.compile, "Compile")
  , (Scope.test, "Test")
  , (Scope.provided, "Provided")
  , (Scope.runtime, "Runtime")
  , (Scope.system, "sbt.Configurations.System")
  , (Scope.Default, "")
  )

  private val mavenScopeAndScope = List(
    ("compile", Scope.compile)
  , ("test", Scope.test)
  , ("provided", Scope.provided)
  , ("runtime", Scope.runtime)
  , ("system", Scope.system)
  , ("", Scope.default)
  )

  override def tests: List[Test] = List(
    example("test scope render", testRender)
  , property("test scope renderWithPrefix", testRenderWithPrefix)
  , example("test scope parse (valid)", testParseValid)
  , property("test scope parse (invalid)", testParseInvalid)
  )

  def testRender: Result = Result.all(for {
      (scope, expected) <- scopeAndRendered
    } yield Scope.render(scope) ==== expected
  )

  def testRenderWithPrefix: Property = for {
    prefix <- Gen.string(Gen.unicode, Range.linear(0, 20)).log("prefix")
  } yield {
    Result.all(
      scopeAndRendered.map { case (scope, expected) =>
        Scope.renderWithPrefix(prefix, scope) ==== (if (scope == Scope.Default) "" else s"$prefix$expected")
      }
    )
  }

  def testParseValid: Result = {
    Result.all(
      mavenScopeAndScope.map { case (mavenScope, scope) =>
          Scope.parse(mavenScope) ==== Right(scope)
      }
    )
  }

  def testParseInvalid: Property = for {
    n <- Gen.int(Range.linear(1, 1000)).log("n")
    mavenScope <- Gen.string(Gen.unicode, Range.linear(1, 50)).map(_ + n).log("mavenScope")
  } yield {
    Result.assert(Scope.parse(mavenScope).isLeft)
  }
}
