package maven2sbt.cli

import java.io.{BufferedWriter, File, FileWriter}

import cats.data._
import cats.implicits._
import cats.effect._

import maven2sbt.core.{Maven2Sbt, Maven2SbtError}
import maven2sbt.effect._
import maven2sbt.effect.CatsIo._

import pirate._
import piratex._


/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
object Maven2SbtApp extends MainIo[Maven2SbtArgs] {

  val cmd: Command[Maven2SbtArgs] =
    Metavar.rewriteCommand(
      Help.rewriteCommand(Maven2SbtArgsParser.rawCmd)
    )

  override def command: Command[Maven2SbtArgs] = cmd

  def toCanonicalFile(file: File): File =
    if (file.isAbsolute) file else file.getCanonicalFile

  override def run(args: Maven2SbtArgs): IO[Either[Maven2SbtError, Unit]] = args match {
    case Maven2SbtArgs.FileArgs(scalaVersion, out, overwrite, pomPath) =>
      for {
        pom <- IO(toCanonicalFile(pomPath))
        buildSbtPath <- IO(toCanonicalFile(out))
        result <-
          (buildSbtPath.exists, overwrite) match {
            case (true, Overwrite.DoNotOverwrite) =>
              IO(Maven2SbtError.outputFileAlreadyExist(buildSbtPath).asLeft)

            case (false, Overwrite.DoNotOverwrite) | (_, Overwrite.DoOverwrite) =>
              IO(new BufferedWriter(new FileWriter(buildSbtPath)))
                .bracket { writer =>
                  (for {
                    buildSbt <- EitherT(Maven2Sbt[IO].buildSbtFromPomFile(scalaVersion, pom))
                    _ <- EitherT(IO(writer.write(buildSbt)) *> IO(().asRight[Maven2SbtError]))
                    _ <- EitherT(putStrLnF[IO](
                        s"""Success] The sbt config file has been successfully written at
                           |  $buildSbtPath
                           |""".stripMargin
                        ) *> IO(().asRight[Maven2SbtError]))
                  } yield ()).value
                } (writer => IO(writer.close()))
          }
      } yield result

    case Maven2SbtArgs.PrintArgs(scalaVersion, pomPath) =>
      (for {
        pom <- EitherT(IO(toCanonicalFile(pomPath).asRight))
        buildSbt <- EitherT(Maven2Sbt[IO].buildSbtFromPomFile(scalaVersion, pom))
        _ <- EitherT(putStrLnF[IO](buildSbt) *> IO(().asRight[Maven2SbtError]))
      } yield ()).value
  }

}
