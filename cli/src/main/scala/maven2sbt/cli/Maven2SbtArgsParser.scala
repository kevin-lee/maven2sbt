package maven2sbt.cli

import java.io.File

import scalaz._
import Scalaz._

import pirate._
import Pirate._

import maven2sbt.core.ScalaVersion
import maven2sbt.info.Maven2SbtBuildInfo

/**
 * @author Kevin Lee
 * @since 2020-01-01
 */
object Maven2SbtArgsParser {

  def fileParser: Parse[Maven2SbtArgs] = Maven2SbtArgs.fileArgs _ |*| ((
      flag[String](
        both('s', "scala-version")
      , metavar("<version>") |+| description("Scala version")
      ).map(ScalaVersion.apply)
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
  ))

  def printParse: Parse[Maven2SbtArgs] = Maven2SbtArgs.printArgs _ |*| ((
      flag[String](
        both('s', "scala-version")
      , metavar("<version>") |+| description("Scala version")
      ).map(ScalaVersion.apply)
    , argument[String](
      metavar("<pom-path>") |+| description("Path to the pom file.")
    ).map(new File(_))
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
