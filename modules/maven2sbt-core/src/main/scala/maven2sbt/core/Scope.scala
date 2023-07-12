package maven2sbt.core

import cats.Show
import cats.kernel.Eq
import cats.syntax.all.*

/** @author Kevin Lee
  * @since 2019-04-21
  */
sealed trait Scope

object Scope {
  case object Compile extends Scope
  case object Test extends Scope
  case object Provided extends Scope
  case object Runtime extends Scope
  case object System extends Scope
  case object Default extends Scope
  final case class Unknown(value: String) extends Scope

  def compile: Scope  = Compile
  def test: Scope     = Test
  def provided: Scope = Provided
  def runtime: Scope  = Runtime
  def system: Scope   = System
  def default: Scope  = Default

  def unknown(value: String): Scope = Unknown(value)

  def all: List[Scope] = List(compile, test, provided, runtime, system)

  implicit val eq: Eq[Scope] = Eq.fromUniversalEquals[Scope]

  implicit val scopeShow: Show[Scope] = render(_)

  def render(scope: Scope): String = scope match {
    case Compile => "Compile"

    case Test => "Test"

    case Provided => "Provided"

    case Runtime => "Runtime"

    case System => "sbt.Configurations.System"

    case Default => ""

    case Unknown(value) => value
  }

  def renderNonCompileWithPrefix(prefix: String, scope: Scope, propsName: Props.PropsName): String =
    if (scope === Scope.compile) {
      ""
    } else {
      scope match {
        case Scope.Unknown(_) =>
          val rendered = render(scope)
          if (rendered.isEmpty) ""
          else {
            val renderedString = StringUtils.renderWithProps(propsName, rendered).toQuotedString
            if (renderedString.isEmpty)
              ""
            else
              s"$prefix$renderedString"
          }
        case Scope.Compile | Scope.Test | Scope.Provided | Scope.Runtime | Scope.System | Scope.Default =>
          val rendered = render(scope)
          if (rendered.isEmpty)
            ""
          else
            s"$prefix$rendered"
      }
    }

  def renderToMaven(scope: Scope): String = scope match {
    case Compile => "compile"

    case Test => "test"

    case Provided => "provided"

    case Runtime => "runtime"

    case System => "system"

    case Default => ""

    case Unknown(value) => value
  }

  def parse(scope: String): Scope = scope match {
    case "compile" => compile

    case "test" => test

    case "provided" => provided

    case "runtime" => runtime

    case "system" => system

    case "" => default

    case unknownScope => unknown(unknownScope)
  }

}
