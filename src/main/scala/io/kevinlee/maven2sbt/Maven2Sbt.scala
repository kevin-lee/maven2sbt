package io.kevinlee.maven2sbt

import scala.language.postfixOps
import scala.xml._

/**
  * @author Kevin Lee
  * @since 2017-04-03
  */
object Maven2Sbt extends App {

  val scalaVersion = "2.11.7"

  val pomXml = getClass.getResourceAsStream("/pom.xml")
  val pom = XML.load(pomXml)

  val groupId = pom \ "groupId" text
  val artifactId = pom \ "artifactId" text
  val version = pom \ "version" text

  val properties: Map[String, String] = (for {
    properties <- pom \ "properties"
    property <- properties.child
    label = property.label
    if !label.startsWith("#PCDATA")
    names = label.split("\\.")
    propertyName = if (names.length == 1) label else names.head + names.tail.map(_.capitalize).mkString
  } yield propertyName -> property.text).toMap


  case class Dependency(groupId: String,
                        artifactId: String,
                        version: String,
                        scope: Option[String]) {
    def toDependencyString: String =
      s"""$groupId" % "$artifactId" % "$version"${scope.fold("")(x => s""" % "$x"""")}"""
  }

  val dependencies: Seq[Dependency] =
    pom \ "dependencies" \ "dependency" map { dependency =>
      val groupId = dependency \ "groupId" text
      val artifactId = dependency \ "artifactId" text
      val version = dependency \ "version" text
      val scope = dependency \ "scope" text

      Dependency(groupId,
                 artifactId,
                 version,
                 Option(scope).filter(_.nonEmpty))
    }

  val libraryDependencies = dependencies match {
    case Seq() => ""
    case x :: Seq() =>  s"""libraryDependencies += "${x.toDependencyString}"""
    case x :: xs =>
      s"""libraryDependencies ++= Seq(
         |  ${x.toDependencyString},
         |${xs.map("  " + _.toDependencyString).mkString(",\n")}
         |)
       """.stripMargin
  }


  val buildSbt =
    s"""
       |name := "$artifactId"
       |
       |organization := "$groupId"
       |
       |version := "$version"
       |
       |scalaVersion := "$scalaVersion"
       |
       |${properties map { case (k, v) => s"""val $k = "$v"""" } mkString "\n"}
       |
       |$libraryDependencies
       |
     """.stripMargin

  println(buildSbt)

}
