package maven2sbt.core

import java.io.{File, InputStream}

import cats._
import cats.data._
import cats.implicits._

import maven2sbt.effect.Effect

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

  def apply[F[_] : Maven2Sbt]: Maven2Sbt[F] = implicitly[Maven2Sbt[F]]

  implicit def maven2SbtF[F[_]](implicit EF0: Effect[F], MF0: Monad[F]): Maven2SbtF[F] = new Maven2SbtF[F] {
    override implicit val EF: Effect[F] = EF0
    override implicit val MF: Monad[F] = MF0
  }

}

trait Maven2SbtF[F[_]] extends Maven2Sbt[F] {

  implicit def EF: Effect[F]
  implicit def MF: Monad[F]

  def fOf[A](a: A): F[A] = EF.effect(a)
  def eitherTF[A, B](e: Either[A, B]): EitherT[F, A, B] = EitherT(EF.effect(e))

  def buildSbt(scalaVersion: ScalaVersion, pomElem: => Elem): F[Either[Maven2SbtError, String]] =
    for {
      pom <- fOf(pomElem)
      ProjectInfo(GroupId(groupId), ArtifactId(artifactId), Version(version)) <- fOf(ProjectInfo.from(pom))
      mavenProperties <- fOf(MavenProperty.from(pom))
      repositories <- fOf(Repository.from(pom))
      dependencies <- fOf(Dependency.from(pom))
      renderedMavenProperties <- fOf(mavenProperties.map(MavenProperty.render).mkString("\n"))
      renderedRepositories <- fOf(Repository.renderToResolvers(repositories, 4))
      renderedDependencies <- fOf(Dependency.renderLibraryDependencies(dependencies, 4))
      buildSbtString <- fOf(
        s"""
           |$renderedMavenProperties
           |
           |ThisBuild / organization := "$groupId"
           |ThisBuild / version := "$version"
           |ThisBuild / scalaVersion := "${scalaVersion.scalaVersion}"
           |
           |lazy val root = (project in file("."))
           |  .settings(
           |    name := "$artifactId"
           |  , $renderedRepositories
           |  , $renderedDependencies
           |  )
           |""".stripMargin)
    } yield buildSbtString.asRight

  def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): F[Either[Maven2SbtError, String]] =
    (for {
      pomFile <- eitherTF(Option(file).filter(_.exists()).toRight(Maven2SbtError.pomFileNotExist(file)))
      pomElem <- eitherTF(XML.loadFile(pomFile).asRight[Maven2SbtError])
      buildSbtString <- EitherT(buildSbt(scalaVersion, pomElem))
    } yield buildSbtString).value

  def buildSbtFromInputStream(scalaVersion: ScalaVersion, pom: InputStream): F[Either[Maven2SbtError, String]] =
    (for {
      inputStream <- eitherTF(Option(pom).toRight(Maven2SbtError.noPomInputStream))
      pomElem <- eitherTF(XML.load(inputStream).asRight[Maven2SbtError])
      buildSbtString <- EitherT(buildSbt(scalaVersion, pomElem))
    } yield buildSbtString).value

}
