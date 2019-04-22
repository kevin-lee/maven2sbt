package kevinlee.maven2sbt

import java.io.InputStream

import scala.xml._

/**
  * @author Kevin Lee
  * @since 2017-04-03
  */
object Maven2Sbt extends App {

  val pomXml: InputStream = getClass.getResourceAsStream("/pom.xml")
  val pom: Elem = XML.load(pomXml)

  def buildSbt(scalaVersion: ScalaVersion, pom: Elem): String = {
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

  println(buildSbt(ScalaVersion("2.11.7"), pom))

}
