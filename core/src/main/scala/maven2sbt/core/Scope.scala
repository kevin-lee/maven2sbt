package maven2sbt.core

import cats.syntax.all._

/**
  * @author Kevin Lee
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

  def compile: Scope = Compile
  def test: Scope = Test
  def provided: Scope = Provided
  def runtime: Scope = Runtime
  def system: Scope = System
  def default: Scope = Default

  def render(scope: Scope): String = scope match {
    case Compile => "Compile"

    case Test => "Test"

    case Provided => "Provided"

    case Runtime => "Runtime"

    case System => "sbt.Configurations.System"

    case Default => ""
  }

  def renderWithPrefix(prefix: String, scope: Scope): String = {
    val rendered = render(scope)
    if (rendered.isEmpty)
      ""
    else
      s"$prefix$rendered"
  }

  def renderToMaven(scope: Scope): String = scope match {
    case Compile => "compile"

    case Test => "test"

    case Provided => "provided"

    case Runtime => "runtime"

    case System => "system"

    case Default => ""
  }

  def parse(scope: String): Either[String, Scope] = scope match {
    case "compile" => compile.asRight[String]

    case "test" => test.asRight[String]

    case "provided" => provided.asRight[String]

    case "runtime" => runtime.asRight[String]

    case "system" => system.asRight[String]

    case "" => default.asRight[String]

    case _ => s"Unsupported scope: $scope".asLeft[Scope]
  }

  def parseUnsafe(scope: String): Scope = parse(scope).fold(sys.error, identity)

}