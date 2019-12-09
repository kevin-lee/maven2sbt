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

  val rawCmd: Command[Maven2SbtArgs] =
    ((Maven2SbtArgs |*| (
      flag[String](long("scalaVersion"), metavar("<version>"))
        .map(ScalaVersion)
    , flag[String](
          both('o', "out")
        , metavar("<output-file>") |+| description("output sbt config file (default: build.sbt)")
        ).default("build.sbt").map(new File(_))
    , switch(
          long("overwrite")
        , description("Overwrite if the output file already exists.")
        ).map(Overwrite.fromBoolean)
    , argument[String](metavar("<pom-path>"))
        .map(new File(_))
    )) <* version(Maven2SbtBuildInfo.version)) ~ "Maven2Sbt" ~~ "A tool to convert Maven pom.xml into sbt build.sbt"

  val cmd: Command[Maven2SbtArgs] =
    Metavar.rewriteCommand(
      Help.rewriteCommand(rawCmd)
    )

  override def command: Command[Maven2SbtArgs] = cmd

  def toCanonicalFile(file: File): File =
    if (file.isAbsolute) file else file.getCanonicalFile

  override def run(args: Maven2SbtArgs): IO[ExitCode \/ Unit] = {
    val pomPath = toCanonicalFile(args.pomPath)
    val buildSbtPath = toCanonicalFile(args.out)
    val result = Maven2Sbt.buildSbtFromPomFile(
        args.scalaVersion
      , pomPath
      )
    result match {
      case Right(buildSbt) =>
        (buildSbtPath.exists, args.overwrite) match {
          case (true, Overwrite.DoNotOverwrite) =>
            handleError(Maven2SbtError.outputFileAlreadyExist(buildSbtPath))

          case (false, Overwrite.DoNotOverwrite) | (_ , Overwrite.DoOverwrite) =>
            IO(new BufferedWriter(new FileWriter(buildSbtPath)))
            .bracket(writer => IO(writer.close())) { writer =>
              IO(writer.write(buildSbt)) *>
                IO(println(
                  s"""Success] The sbt config file has been successfully written at
                     |$buildSbtPath
                     |""".stripMargin
                ).right[ExitCode])
            }
        }
      case Left(error) =>
        handleError(error)
    }
  }

  private def handleError(error: Maven2SbtError): IO[ExitCode \/ Unit] =
    IO(System.err.println(s"ERROR] ${Maven2SbtError.render(error)}\n")) *>
      IO(ExitCode.failure(1).left)

}
