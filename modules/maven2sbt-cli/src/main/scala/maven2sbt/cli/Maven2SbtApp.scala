package maven2sbt.cli

import cats.effect.*
import cats.syntax.all.*
import effectie.instances.ce3.fx.ioFx
import effectie.instances.console.consoleEffectF
import extras.cats.syntax.all.*
import maven2sbt.core.{BuildSbt, Maven2Sbt, Maven2SbtError}
import pirate.*
import piratex.*

import java.io.{BufferedWriter, File, FileWriter}

/** @author Kevin Lee
  * @since 2019-12-08
  */
@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
object Maven2SbtApp extends MainIo[Maven2SbtArgs] {

  val maven2SbtIo: Maven2Sbt[IO] = Maven2Sbt[IO]

  val cmd: Command[Maven2SbtArgs] =
    Metavar.rewriteCommand(
      Help.rewriteCommand(Maven2SbtArgsParser.rawCmd)
    )

  override def command: Command[Maven2SbtArgs] = cmd

  override def prefs: Prefs = DefaultPrefs().copy(width = 100)

  def toCanonicalFile(file: File): File =
    if (file.isAbsolute) file else file.getCanonicalFile

  override def runApp(args: Maven2SbtArgs): IO[Either[Maven2SbtError, Option[String]]] = args match {
    case Maven2SbtArgs.FileArgs(scalaVersion, scalaBinaryVersionName, propsName, libsName, out, overwrite, pomPath) =>
      for {
        pom          <- IO(toCanonicalFile(pomPath))
        buildSbtPath <- IO(toCanonicalFile(out))
        result       <-
          (buildSbtPath.exists, overwrite) match {
            case (true, Overwrite.DoNotOverwrite) =>
              IO(Maven2SbtError.outputFileAlreadyExist(buildSbtPath).asLeft[Option[String]])

            case (false, Overwrite.DoNotOverwrite) | (_, Overwrite.DoOverwrite) =>
              IO(new BufferedWriter(new FileWriter(buildSbtPath)))
                .bracket { writer =>
                  (for {
                    buildSbt       <- maven2SbtIo
                                        .buildSbtFromPomFile(
                                          scalaVersion,
                                          propsName,
                                          scalaBinaryVersionName,
                                          pom
                                        )
                                        .eitherT
                    buildSbtString <- IO(BuildSbt.render(buildSbt, propsName, libsName)).rightT
                    _              <- IO(writer.write(buildSbtString)).rightT[Maven2SbtError]
                    result =
                      s"""Success] The sbt config file has been successfully written at
                                             |  ${buildSbtPath.getCanonicalPath}
                                             |""".stripMargin

                  } yield result.some).value
                }(writer => IO(writer.close()))
          }
      } yield result

    case Maven2SbtArgs.PrintArgs(scalaVersion, scalaBinaryVersionName, propsName, libsName, pomPath) =>
      (for {
        pom            <- IO(toCanonicalFile(pomPath)).rightT
        buildSbt       <- maven2SbtIo.buildSbtFromPomFile(scalaVersion, propsName, scalaBinaryVersionName, pom).eitherT
        buildSbtString <- IO(BuildSbt.render(buildSbt, propsName, libsName)).rightT[Maven2SbtError]

      } yield buildSbtString.some).value
  }

}
