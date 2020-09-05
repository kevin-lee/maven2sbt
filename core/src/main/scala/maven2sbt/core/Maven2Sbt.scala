package maven2sbt.core

import java.io.{File, InputStream}

import cats._
import cats.data._
import cats.syntax.all._

import effectie.Effectful._
import effectie.cats._
import effectie.cats.EitherTSupport._

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

  def apply[F[_]: Monad: EffectConstructor: ConsoleEffect]: Maven2Sbt[F] = new Maven2SbtF[F]

  final class Maven2SbtF[F[_]: Monad: EffectConstructor: ConsoleEffect]
    extends Maven2Sbt[F] {

    def buildSbt(scalaVersion: ScalaVersion, pomElem: => Elem): F[Either[Maven2SbtError, BuildSbt]] =
      for {
        pom <- effectOf(pomElem)
        ProjectInfo(groupId, artifactId, version) <- effectOf(ProjectInfo.from(pom))
        mavenProperties <- effectOf(MavenProperty.from(pom))
        props <- effectOf(mavenProperties.map(BuildSbt.Prop.fromMavenProperty))
        repositories <- effectOf(Repository.from(pom))
        dependencies <- effectOf(Dependency.from(pom))
        globalSettings <- effectOfPure(BuildSbt.GlobalSettings.empty)
        thisBuildSettings <- effectOfPure(
            BuildSbt.ThisBuildSettings(BuildSbt.Settings(
                groupId.some
              , none[ArtifactId]
              , version.some
              , scalaVersion.some
              , List.empty[Repository]
              , List.empty[Dependency]
            ))
          )
        projectSettings <- effectOfPure(
            BuildSbt.ProjectSettings(BuildSbt.Settings(
                none[GroupId]
              , artifactId.some
              , none[Version]
              , none[ScalaVersion]
              , repositories.toList
              , dependencies.toList
            ))
          )
        buildSbtData <- effectOf(
            BuildSbt(
              globalSettings
            , thisBuildSettings
            , projectSettings
            , props.toList
            )
          )
      } yield buildSbtData.asRight

    def buildSbtFromPomFile(scalaVersion: ScalaVersion, file: File): F[Either[Maven2SbtError, BuildSbt]] =
      (for {
        pomFile <- eitherTOf(Option(file).filter(_.exists()).toRight(Maven2SbtError.pomFileNotExist(file)))
        pomElem <- eitherTRight(XML.loadFile(pomFile))
        buildSbtString <- EitherT(buildSbt(scalaVersion, pomElem))
      } yield buildSbtString).value

    def buildSbtFromInputStream(scalaVersion: ScalaVersion, pom: InputStream): F[Either[Maven2SbtError, BuildSbt]] =
      (for {
        inputStream <- eitherTOf(Option(pom).toRight(Maven2SbtError.noPomInputStream))
        pomElem <- eitherTRight(XML.load(inputStream))
        buildSbtString <- EitherT(buildSbt(scalaVersion, pomElem))
      } yield buildSbtString).value

  }

}
