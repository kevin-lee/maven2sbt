package maven2sbt.cli

import java.io.File
import scalaz._
import Scalaz._
import pirate._
import Pirate._
import maven2sbt.core.{Libs, Props, ScalaVersion}
import maven2sbt.info.Maven2SbtBuildInfo

/**
 * @author Kevin Lee
 * @since 2020-01-01
 */
object Maven2SbtArgsParser {

  private val scalaVersionArg: Parse[ScalaVersion] = flag[String](
    both('s', "scala-version")
    , metavar("<version>") |+| description("Scala version")
  ).map(ScalaVersion.apply)

  private val paramsNameArg: Parse[Props.PropsName] = flag[String](
    long("props-name")
    , metavar("<props-name>") |+| description("properties object name (e.g. 'props' in `lazy val props = new {}`) (default: props)")
  ).default("props").map(Props.PropsName.apply)

  private val libsNameArg: Parse[Libs.LibsName] = flag[String](
    long("libs-name")
    , metavar("<libs-name>") |+| description("The name of the object containing all the libraries to re-use (e.g. 'libs' in `lazy val libs = new {}`) (default: libs)")
  ).default("libs").map(Libs.LibsName.apply)

  private val pomPathArg: Parse[File] = argument[String](
    metavar("<pom-path>") |+| description("Path to the pom file.")
  ).map(new File(_))

  def fileParser: Parse[Maven2SbtArgs] = Maven2SbtArgs.fileArgs _ |*| ((
      scalaVersionArg
    , paramsNameArg
    , libsNameArg
    , flag[String](
        both('o', "out")
      , metavar("<file>") |+| description("output sbt config file (default: build.sbt)")
      ).default("build.sbt").map(new File(_))
    , switch(
        long("overwrite")
      , description("Overwrite if the output file already exists.")
      ).map(Overwrite.fromBoolean)
    , pomPathArg
  ))

  def printParse: Parse[Maven2SbtArgs] = Maven2SbtArgs.printArgs _ |*| ((
      scalaVersionArg
    , paramsNameArg
    , libsNameArg
    , pomPathArg
  ))

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

}
