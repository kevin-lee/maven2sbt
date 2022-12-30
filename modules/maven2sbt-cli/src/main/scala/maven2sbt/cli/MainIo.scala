package maven2sbt.cli

import cats.effect.*
import effectie.core.ConsoleEffect
import effectie.instances.ce3.fx.ioFx
import effectie.instances.console.consoleEffectF
import maven2sbt.core.Maven2SbtError
import pirate.{ExitCode as PirateExitCode, *}
import scalaz.*

/** @author Kevin Lee
  * @since 2019-12-09
  */
@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
trait MainIo[A] extends IOApp {

  type SIO[X] = scalaz.effect.IO[X]

  def command: Command[A]

  def runApp(args: A): IO[Either[Maven2SbtError, Unit]]

  def prefs: Prefs = DefaultPrefs().copy(descIndent = 33, width = 100)

  def exitWith[X](exitCode: ExitCode): IO[X] =
    IO(sys.exit(exitCode.code))

  def exitWithPirate[X](exitCode: PirateExitCode): IO[X] =
    IO(exitCode.fold(sys.exit(0), sys.exit(_)))

  def getArgs(args: List[String], command: Command[A], prefs: Prefs): IO[PirateExitCode \/ A] =
    IO(Runners.runWithExit[A](args, command, prefs).unsafePerformIO())

  override def run(args: List[String]): IO[ExitCode] = for {
    codeOrA       <- getArgs(args, command, prefs)
    errorOrResult <- codeOrA.fold[IO[Either[Maven2SbtError, Unit]]](exitWithPirate, runApp)
    exitCode      <- errorOrResult.fold(
                       err =>
                         ConsoleEffect[IO].putErrStrLn(Maven2SbtError.render(err)) *>
                           exitWith(ExitCode.Error),
                       _ => IO(ExitCode.Success)
                     )
  } yield exitCode

}
