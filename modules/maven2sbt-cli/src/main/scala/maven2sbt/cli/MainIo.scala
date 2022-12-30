package maven2sbt.cli

import cats.effect.*
import cats.syntax.all.*
import effectie.core.ConsoleEffect
import effectie.instances.ce3.fx.ioFx
import effectie.instances.console.consoleEffectF
import maven2sbt.cli.Maven2SbtArgsParser.ArgParseFailureResult
import maven2sbt.core.Maven2SbtError
import pirate.{Command, Prefs}

/** @author Kevin Lee
  * @since 2019-12-09
  */
@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
trait MainIo[A] extends IOApp {

  type SIO[X] = scalaz.effect.IO[X]

  def command: Command[A]

  def runApp(args: A): IO[Either[Maven2SbtError, Option[String]]]

  def prefs: Prefs

  def exitCodeToEither(argParseFailureResult: ArgParseFailureResult): IO[Either[Maven2SbtError, Option[String]]] =
    argParseFailureResult match {
      case err @ ArgParseFailureResult.JustMessageOrHelp(_) =>
        IO.pure(err.show.some.asRight[Maven2SbtError])
      case err @ ArgParseFailureResult.ArgParseError(_) =>
        IO(Maven2SbtError.argParse(err.errors).asLeft[Option[String]])
    }

  override def run(args: List[String]): IO[ExitCode] = {
    def getArgs(
      args: List[String],
      command: Command[A],
      prefs: Prefs,
    ): IO[Either[ArgParseFailureResult, A]] = {
      import pirate.{Interpreter, Usage}
      import scalaz.{-\/, \/-}
      Interpreter.run(command.parse, args, prefs) match {
        case (ctx, -\/(e)) =>
          IO(
            Usage
              .printError(command, ctx, e, prefs)
              .fold[ArgParseFailureResult](
                ArgParseFailureResult.ArgParseError(_),
                ArgParseFailureResult.JustMessageOrHelp(_),
              )
              .asLeft[A],
          )
        case (_, \/-(v)) =>
          IO(v.asRight[ArgParseFailureResult])
      }
    }
    for {
      codeOrA       <- getArgs(args, command, prefs)
      errorOrResult <- codeOrA.fold(exitCodeToEither, runApp)
      exitCode      <- errorOrResult.fold(
                         err =>
                           ConsoleEffect[IO].putErrStrLn(Maven2SbtError.render(err)) *>
                             IO.pure(ExitCode.Error),
                         _.fold(IO.pure(ExitCode.Success))(msg =>
                           ConsoleEffect[IO].putStrLn(msg) *> IO.pure(ExitCode.Success)
                         )
                       )
    } yield exitCode
  }

}
