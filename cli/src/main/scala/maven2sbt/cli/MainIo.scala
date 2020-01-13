package maven2sbt.cli


import cats.effect._
import cats.implicits._

import maven2sbt.core.Maven2SbtError
import maven2sbt.effect._
import maven2sbt.effect.CatsIo._

import pirate.{ExitCode => PirateExitCode, _}
import scalaz._

/**
 * @author Kevin Lee
 * @since 2019-12-09
 */
trait MainIo[A] {

  type SIO[X] = scalaz.effect.IO[X]

  def command: Command[A]

  def run(args: A): IO[Either[Maven2SbtError, Unit]]

  def prefs: Prefs = DefaultPrefs()

  def exitWith[X](exitCode: ExitCode): IO[X] =
    IO(sys.exit(exitCode.code))

  def exitWithPirate[X](exitCode: PirateExitCode): IO[X] =
    IO(exitCode.fold(sys.exit(0), sys.exit(_)))

  def getArgs(args: Array[String], command: Command[A], prefs: Prefs): IO[PirateExitCode \/ A] =
    IO(Runners.runWithExit[A](args.toList, command, prefs).unsafePerformIO())

  def main(args: Array[String]): Unit = (for {
    codeOrA <- getArgs(args, command, prefs)
    errorOrResult <- codeOrA.fold[IO[Either[Maven2SbtError, Unit]]](exitWithPirate, run)
    _ <- errorOrResult.fold(
        err => putErrStrLnF[IO](Maven2SbtError.render(err)) *>
          exitWith(ExitCode.Error)
        , IO(_)
      )
  } yield ())
    .unsafeRunSync()

}
