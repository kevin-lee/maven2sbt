package kevinlee.maven2sbt

import java.io.File

import scala.xml._

/**
  * @author Kevin Lee
  * @since 2017-04-03
  */
object Maven2Sbt extends App {

  def buildSbt(scalaVersion: ScalaVersion, pom: => Elem): String = {
    val ProjectInfo(GroupId(groupId), ArtifactId(artifactId), Version(version)) =
      ProjectInfo.from(pom)
    s"""
       |${MavenProperty.from(pom).map(MavenProperty.render).mkString("\n")}
       |
       |ThisBuild / organization := "$groupId"
       |ThisBuild / version := "$version"
       |ThisBuild / scalaVersion := "${scalaVersion.scalaVersion}"
       |
       |lazy val root = (project in file("."))
       |  .settings(
       |    name := "$artifactId"
       |  , ${Repository.renderToResolvers(Repository.from(pom), 4)}
       |  , ${Dependency.renderLibraryDependencies(Dependency.from(pom), 4)}
       |  )
       |""".stripMargin
  }

  def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): String =
    buildSbt(
      scalaVersion
    , XML.loadFile(file)
    )

  // TODO: Remove code below once a proper CLI is done.
  (for {
      Array(scalaVersion, path) <- Option(args)
      file = new File(path)
      pomFile = if (!file.isAbsolute) file.getCanonicalFile else file
    } yield (scalaVersion, pomFile)) match {
    case Some((scalaVersion, file)) =>
      println(buildSbtFromPomFile(ScalaVersion(scalaVersion), file))
    case None =>
      println(
        s"""Please enter [scalaVersion] and [pom file path].
           |e.g.)
           |sbt "run 2.12.10 src/main/resources/pom.xml"
           |""".stripMargin)
  }


}
