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
  def buildSbt(scalaVersion: ScalaVersion, pom: => Elem): F[Either[Maven2SbtError, BuildSbt]]
  def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): F[Either[Maven2SbtError, BuildSbt]]
  def buildSbtFromInputStream(scalaVersion: ScalaVersion, pom: InputStream): F[Either[Maven2SbtError, BuildSbt]]
}

object Maven2Sbt {

  def apply[F[_] : Maven2Sbt]: Maven2Sbt[F] = implicitly[Maven2Sbt[F]]

  implicit def maven2SbtF[F[_] : EffectConstructor : ConsoleEffect : Monad]: Maven2Sbt[F] =
    new Maven2SbtF[F]

  final class Maven2SbtF[F[_] : Monad : EffectConstructor : ConsoleEffect]
    extends Maven2Sbt[F]
    with Effectful
    with ConsoleEffectful
    with EitherTSupport {

    def buildSbt(scalaVersion: ScalaVersion, pomElem: => Elem): F[Either[Maven2SbtError, BuildSbt]] =
      for {
        pom <- effect(pomElem)
        ProjectInfo(groupId, artifactId, version) <- effect(ProjectInfo.from(pom))
        mavenProperties <- effect(MavenProperty.from(pom))
        props <- effect(mavenProperties.map(BuildSbt.Prop.fromMavenProperty))
        repositories <- effect(Repository.from(pom))
        dependencies <- effect(Dependency.from(pom))
        buildSbtData <- effect(
          BuildSbt(
            BuildSbt.GlobalSettings(BuildSbt.Settings(
              none[GroupId]
            , none[ArtifactId]
            , none[Version]
            , none[ScalaVersion]
            , List.empty[Repository]
            , List.empty[Dependency]
            ))
          , BuildSbt.ThisBuildSettings(BuildSbt.Settings(
              groupId.some
            , none[ArtifactId]
            , version.some
            , scalaVersion.some
            , List.empty[Repository]
            , List.empty[Dependency]
            ))
          , BuildSbt.ProjectSettings(BuildSbt.Settings(
              none[GroupId]
            , artifactId.some
            , none[Version]
            , none[ScalaVersion]
            , repositories.toList
            , dependencies.toList
            ))
          , props.toList
          ))
      } yield buildSbtData.asRight

    def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): F[Either[Maven2SbtError, BuildSbt]] =
      (for {
        pomFile <- eitherTF(Option(file).filter(_.exists()).toRight(Maven2SbtError.pomFileNotExist(file)))
        pomElem <- eitherTEffect[F, Maven2SbtError, Elem](XML.loadFile(pomFile))
        buildSbtString <- EitherT(buildSbt(scalaVersion, pomElem))
      } yield buildSbtString).value

    def buildSbtFromInputStream(scalaVersion: ScalaVersion, pom: InputStream): F[Either[Maven2SbtError, BuildSbt]] =
      (for {
        inputStream <- eitherTF(Option(pom).toRight(Maven2SbtError.noPomInputStream))
        pomElem <- eitherTEffect[F, Maven2SbtError, Elem](XML.load(inputStream))
        buildSbtString <- EitherT(buildSbt(scalaVersion, pomElem))
      } yield buildSbtString).value

  }

}

