package maven2sbt.cli

import java.io.{BufferedWriter, File, FileWriter}

import pirate._
import Pirate._
import piratex._

import scalaz._
import Scalaz._

import cats.effect._

import maven2sbt.core.{Maven2Sbt, Maven2SbtError, ScalaVersion}
import maven2sbt.info.Maven2SbtBuildInfo
import maven2sbt.effect._


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

  override def run(args: Maven2SbtArgs): IO[Maven2SbtError \/ Unit] = args match {
    case Maven2SbtArgs.FileArgs(scalaVersion, out, overwrite, pomPath) =>
      for {
        pom <- IO(toCanonicalFile(pomPath))
        buildSbtPath <- IO(toCanonicalFile(out))
        result <- Maven2Sbt[IO].buildSbtFromPomFile(scalaVersion, pom)
        r <- result match {
            case Right(buildSbt) =>
              (buildSbtPath.exists, overwrite) match {
                case (true, Overwrite.DoNotOverwrite) =>
                  IO(Maven2SbtError.outputFileAlreadyExist(buildSbtPath).left)

                case (false, Overwrite.DoNotOverwrite) | (_, Overwrite.DoOverwrite) =>
                  IO(new BufferedWriter(new FileWriter(buildSbtPath)))
                    .bracket { writer =>
                      for {
                        _ <- IO(writer.write(buildSbt))
                        _ <- putStrLnF[IO](
                          s"""Success] The sbt config file has been successfully written at
                             |  $buildSbtPath
                             |""".stripMargin
                          )
                      } yield ().right[Maven2SbtError]
                    } (writer => IO(writer.close()))
              }
            case Left(error) =>
              IO(error.left)
          }
      } yield r

    case Maven2SbtArgs.PrintArgs(scalaVersion, pomPath) =>
      for {
        pom <- IO(toCanonicalFile(pomPath))
        result <- Maven2Sbt[IO].buildSbtFromPomFile(scalaVersion, pom)
        r <- result match {
            case Right(buildSbt) =>
              putStrLnF(
                buildSbt
              ) *> IO(().right[Maven2SbtError])
            case Left(error) =>
              IO(error.left)
          }
      } yield r
  }

}
