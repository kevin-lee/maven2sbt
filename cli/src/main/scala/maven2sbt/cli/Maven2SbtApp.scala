package maven2sbt.cli

import java.io.{BufferedWriter, File, FileWriter}

import pirate._
import Pirate._
import piratex._

import scalaz._
import Scalaz._

import scalaz.effect.IO

import maven2sbt.core.{Maven2Sbt, Maven2SbtError, ScalaVersion}
import maven2sbt.info.Maven2SbtBuildInfo


/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
object Maven2SbtApp extends MainIO[Maven2SbtArgs] {

  def fileParser: Parse[Maven2SbtArgs] = Maven2SbtArgs.fileArgs _ |*| (
    flag[String](
        both('s', "scala-version")
      , metavar("<version>") |+| description("Scala version")
      ).map(ScalaVersion)
    , flag[String](
        both('o', "out")
      , metavar("<file>") |+| description("output sbt config file (default: build.sbt)")
      ).default("build.sbt").map(new File(_))
    , switch(
        long("overwrite")
      , description("Overwrite if the output file already exists.")
      ).map(Overwrite.fromBoolean)
    , argument[String](
        metavar("<pom-path>") |+| description("Path to the pom file.")
      ).map(new File(_))
  )

  def printParse: Parse[Maven2SbtArgs] = Maven2SbtArgs.printArgs _ |*| (
    flag[String](
        both('s', "scala-version")
    , metavar("<version>") |+| description("Scala version")
    ).map(ScalaVersion)
  , argument[String](
      metavar("<pom-path>") |+| description("Path to the pom file.")
    ).map(new File(_))
  )

  val rawCmd: Command[Maven2SbtArgs] =
    Command(
      "Maven2Sbt"
    , "A tool to convert Maven pom.xml into sbt build.sbt".some
    , (subcommand(
        Command(
          "file"
        , "Convert pom.xml to sbt config and save in the file".some
        , fileParser
        )
      ) ||| subcommand(
        Command(
          "print"
        , "Convert pom.xml to sbt config and print it out".some
        , printParse
        )
      )) <* version(Maven2SbtBuildInfo.version)
    )

  val cmd: Command[Maven2SbtArgs] =
    Metavar.rewriteCommand(
      Help.rewriteCommand(rawCmd)
    )

  override def command: Command[Maven2SbtArgs] = cmd

  def toCanonicalFile(file: File): File =
    if (file.isAbsolute) file else file.getCanonicalFile

  override def run(args: Maven2SbtArgs): IO[ExitCode \/ Unit] = args match {
    case Maven2SbtArgs.FileArgs(scalaVersion, out, overwrite, pomPath) =>
      val pom = toCanonicalFile(pomPath)
      val buildSbtPath = toCanonicalFile(out)
      val result = Maven2Sbt.buildSbtFromPomFile(
        scalaVersion
        , pom
      )
      result match {
        case Right(buildSbt) =>
          (buildSbtPath.exists, overwrite) match {
            case (true, Overwrite.DoNotOverwrite) =>
              handleError(Maven2SbtError.outputFileAlreadyExist(buildSbtPath))

            case (false, Overwrite.DoNotOverwrite) | (_, Overwrite.DoOverwrite) =>
              IO(new BufferedWriter(new FileWriter(buildSbtPath)))
                .bracket(writer => IO(writer.close())) { writer =>
                  for {
                    _ <- IO(writer.write(buildSbt))
                    _ <- IO.putStrLn(
                      s"""Success] The sbt config file has been successfully written at
                         |  $buildSbtPath

                         |""".
                        stripMargin
                      )
                  } yield ().right[ExitCode]
                }
          }
        case Left(error) =>
          handleError(error)
      }

    case Maven2SbtArgs.PrintArgs(scalaVersion, pomPath) =>
      val pom = toCanonicalFile(pomPath)
      val result = Maven2Sbt.buildSbtFromPomFile(
        scalaVersion
        , pom
      )
      result match {
        case Right(buildSbt) =>
          IO.putStrLn(
            buildSbt
          ) *> IO(().right[ExitCode])
        case Left(
        error) =>
          handleError(error)
      }
  }

  private def handleError(error: Maven2SbtError): IO[ExitCode \/ Unit] =
    IO(System.err.println(s"ERROR] ${Maven2SbtError.render(error)}\n")) *>
      IO(ExitCode.failure(1).left)

}
