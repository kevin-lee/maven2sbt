package maven2sbt.cli

import java.io.{BufferedWriter, File, FileWriter}

import cats.data._
import cats.implicits._
import cats.effect._

import maven2sbt.core.{BuildSbt, Maven2Sbt, Maven2SbtError}
import maven2sbt.effect._

import pirate._
import piratex._


/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
object Maven2SbtApp extends MainIo[Maven2SbtArgs] {

  val maven2SbtIo: Maven2Sbt[IO] = Maven2Sbt[IO]

  val cmd: Command[Maven2SbtArgs] =
    Metavar.rewriteCommand(
      Help.rewriteCommand(Maven2SbtArgsParser.rawCmd)
    )

  override def command: Command[Maven2SbtArgs] = cmd

  def toCanonicalFile(file: File): File =
    if (file.isAbsolute) file else file.getCanonicalFile

  def eitherTLift[A](ioa: IO[A]): EitherT[IO, Maven2SbtError, A] =
    EitherT.liftF[IO, Maven2SbtError, A](ioa)

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
                    buildSbt <- EitherT(maven2SbtIo.buildSbtFromPomFile(scalaVersion, pom))
                    buildSbtString <- eitherTLift(IO(BuildSbt.render(buildSbt)))
                    _ <- eitherTLift(IO(writer.write(buildSbtString)))
                    _ <- eitherTLift(
                        ConsoleEffect[IO].putStrLn(
                          s"""Success] The sbt config file has been successfully written at
                             |  $buildSbtPath
                             |""".stripMargin
                        )
                      )
                  } yield ()).value
                } (writer => IO(writer.close()))
          }
      } yield result

    case Maven2SbtArgs.PrintArgs(scalaVersion, pomPath) =>
      (for {
        pom <- eitherTLift(IO(toCanonicalFile(pomPath)))
        buildSbt <- EitherT(maven2SbtIo.buildSbtFromPomFile(scalaVersion, pom))
        buildSbtString <- eitherTLift[String](IO(BuildSbt.render(buildSbt)))
        _ <- eitherTLift[Unit](ConsoleEffect[IO].putStrLn(buildSbtString))
      } yield ()).value
  }

}
