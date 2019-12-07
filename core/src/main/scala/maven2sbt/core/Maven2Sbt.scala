package maven2sbt.core

import java.io.File

import scala.xml._

/**
  * @author Kevin Lee
  * @since 2017-04-03
  */
object Maven2Sbt {

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

}
