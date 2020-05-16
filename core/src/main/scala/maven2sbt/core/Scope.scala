package maven2sbt.core

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
    case Compile =>
      "Compile"
    case Test =>
      "Test"
    case Provided =>
      "Provided"
    case Runtime =>
      "Runtime"
    case System =>
      "sbt.Configurations.System"
    case Default =>
      ""
  }

  def renderWithPrefix(prefix: String, scope: Scope): String = {
    val rendered = render(scope)
    if (rendered.isEmpty)
      ""
    else
      s"$prefix$rendered"
  }

  def renderToMaven(scope: Scope): String = scope match {
    case Compile =>
      "compile"
    case Test =>
      "test"
    case Provided =>
      "provided"
    case Runtime =>
      "runtime"
    case System =>
      "system"
    case Default =>
      ""
  }

  def parse(scope: String): Either[String, Scope] = scope match {
    case "compile" =>
      Right(compile)
    case "test" =>
      Right(test)
    case "provided" =>
      Right(provided)
    case "runtime" =>
      Right(runtime)
    case "system" =>
      Right(system)
    case "" =>
      Right(default)
    case _ =>
      Left(s"Unsupported scope: $scope")
  }

  def parseUnsafe(scope: String): Scope =
    parse(scope).fold(sys.error, identity)
}