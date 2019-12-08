package maven2sbt.cli

import java.io.File

import pirate._
import Pirate._
import piratex._

import scalaz._, Scalaz._

import maven2sbt.core.{Maven2Sbt, ScalaVersion}
import maven2sbt.info.Maven2SbtBuildInfo

/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
object Maven2SbtApp extends PirateMain[Maven2SbtArgs] {

  val rawCmd: Command[Maven2SbtArgs] =
    ((Maven2SbtArgs |*| (
      flag[String](long("scalaVersion"), metavar("<version>")).map(ScalaVersion)
    , argument[String](metavar("<pom-path>"))
    )) <* version(Maven2SbtBuildInfo.version)) ~ "Maven2Sbt" ~~ "A tool to convert Maven pom.xml into sbt build.sbt"

  val cmd: Command[Maven2SbtArgs] =
    Metavar.rewriteCommand(
      Help.rewriteCommand(rawCmd)
    )

  override def command: Command[Maven2SbtArgs] = cmd

  override def run(args: Maven2SbtArgs): Unit = args match {
    case Maven2SbtArgs(scalaVersion, pomPath) =>
      val path = new File(pomPath)
      val result = Maven2Sbt.buildSbtFromPomFile(
        scalaVersion
      , if (path.isAbsolute) path else path.getCanonicalFile
      )
      println(result)
  }
}
