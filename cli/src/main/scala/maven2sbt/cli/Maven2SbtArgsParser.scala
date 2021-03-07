package maven2sbt.cli

import scalaz._
import Scalaz._
import pirate._
import Pirate._
import maven2sbt.core.{Libs, Props, ScalaBinaryVersion, ScalaVersion}
import maven2sbt.info.Maven2SbtBuildInfo

import java.io.File

/**
 * @author Kevin Lee
 * @since 2020-01-01
 */
object Maven2SbtArgsParser {

  private val scalaVersionArg: Parse[ScalaVersion] = flag[String](
    both('s', "scala-version")
    , metavar("<version>") |+| description("Scala version")
  ).map(ScalaVersion.apply)

  private val scalaBinaryVersionArg: Parse[Option[ScalaBinaryVersion.Name]] = flag[String](
    both('b', "scala-binary-version-name")
    , metavar("<scala-binary-version-name>") |+|
        description(
          s"""The name of Scala binary version property. This is useful to figure out if it is a Scala library or Java library
             |e.g.)
             |-b scala.binary
             |# or
             |--scala-binary-version-name scala.binary
             |---
             |<properties>
             |  <scala.binary>2.13</scala.binary>
             |</properties>
             |<dependencies>
             |  <dependency>
             |    <groupId>io.kevinlee</groupId>
             |    <artifactId>myLib1_$${scala.binary}</artifactId>
             |    <version>0.1.0</version>
             |  </dependency>
             |  <dependency>
             |    <groupId>io.kevinlee</groupId>
             |    <artifactId>myLib2</artifactId>
             |    <version>0.2.0</version>
             |  </dependency>
             |</dependencies>
             |---
             |results in
             |"io.kevinlee" %% "myLib1" % "0.1.0"
             |"io.kevinlee" % "myLib2" % "0.1.0"
             |---
             |""".stripMargin
        )
  ).option.map(_.map(ScalaBinaryVersion.Name(_)))

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
    , scalaBinaryVersionArg
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
    , scalaBinaryVersionArg
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
