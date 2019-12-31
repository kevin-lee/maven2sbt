package maven2sbt.core

import java.io.{File, InputStream}

import cats._
import cats.implicits._

import scala.xml._

/**
  * @author Kevin Lee
  * @since 2017-04-03
  */
trait Maven2Sbt[F[_]] {
  def buildSbt(scalaVersion: ScalaVersion, pom: => Elem): F[Either[Maven2SbtError, String]]
  def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): F[Either[Maven2SbtError, String]]
  def buildSbtFromInputStream(scalaVersion: ScalaVersion, pom: InputStream): F[Either[Maven2SbtError, String]]
}

object Maven2Sbt {

  def apply[F[_]: Maven2Sbt]: Maven2Sbt[F] = implicitly[Maven2Sbt[F]]

  implicit def maven2SbtF[F[_]](implicit M0: Monad[F]): Maven2SbtF[F] = new Maven2SbtF[F] {
    override implicit def FM: Monad[F] = M0
  }

}

trait Maven2SbtF[F[_]] extends Maven2Sbt[F] {

  implicit def FM: Monad[F]

  def buildSbt(scalaVersion: ScalaVersion, pom: => Elem): F[Either[Maven2SbtError, String]] = FM.pure {
    val ProjectInfo(GroupId(groupId), ArtifactId(artifactId), Version(version)) =
      ProjectInfo.from(pom)

    val buildSbt =
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
    Right(buildSbt)
  }

  def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): F[Either[Maven2SbtError, String]] =
    if (file.exists())
      buildSbt(
        scalaVersion
      , XML.loadFile(file)
      )
    else
      FM.pure(Left(Maven2SbtError.pomFileNotExist(file)))

  def buildSbtFromInputStream(scalaVersion: ScalaVersion, pom: InputStream): F[Either[Maven2SbtError, String]] =
    Option(pom).fold(
      FM.pure(Maven2SbtError.noPomInputStream.asLeft[String]))(
      inputStream => buildSbt(scalaVersion, XML.load(inputStream))
    )

}
