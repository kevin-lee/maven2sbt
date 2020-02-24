package maven2sbt.core

import java.io.{File, InputStream}

import cats._
import cats.data._
import cats.implicits._

import maven2sbt.effect._

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

  implicit def maven2SbtF[F[_] : EffectConstructor : ConsoleEffect : Monad]: Maven2Sbt[F] =
    new Maven2SbtF[F]

  final class Maven2SbtF[F[_] : Monad](
    override implicit protected val EF: EffectConstructor[F]
  , override implicit protected val CF: ConsoleEffect[F]
  ) extends Maven2Sbt[F] with Effectful[F] with ConsoleEffectful[F] {

    def eitherTF[A, B](e: => Either[A, B]): EitherT[F, A, B] = EitherT(effect(e))

    def buildSbt(scalaVersion: ScalaVersion, pomElem: => Elem): F[Either[Maven2SbtError, String]] =
      for {
        pom <- effect(pomElem)
        ProjectInfo(GroupId(groupId), ArtifactId(artifactId), Version(version)) <- effect(ProjectInfo.from(pom))
        mavenProperties <- effect(MavenProperty.from(pom))
        repositories <- effect(Repository.from(pom))
        dependencies <- effect(Dependency.from(pom))
        renderedMavenProperties <- effect(mavenProperties.map(MavenProperty.render).mkString("\n"))
        renderedRepositories <- effect(Repository.renderToResolvers(repositories, 4))
        renderedDependencies <- effect(Dependency.renderLibraryDependencies(dependencies, 4))
        buildSbtString <- effect(
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

}

