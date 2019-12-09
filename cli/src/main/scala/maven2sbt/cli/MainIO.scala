package maven2sbt.cli

import pirate.{Command, DefaultPrefs, ExitCode, Prefs, Runners}
import scalaz._
import scalaz.effect.IO

/**
 * @author Kevin Lee
 * @since 2019-12-09
 */
trait MainIO[A]  {
  def command: Command[A]

  def run(a: A): IO[ExitCode \/ Unit]

  def prefs: Prefs = DefaultPrefs()

  def main(args: Array[String]): Unit =
    Runners.runWithExit(args.toList, command, prefs)
      .flatMap(_.fold[IO[ExitCode \/ Unit]](ExitCode.exitWith, run))
      .flatMap(_.fold[IO[Unit]](ExitCode.exitWith, IO(_)))
      .unsafePerformIO
}
