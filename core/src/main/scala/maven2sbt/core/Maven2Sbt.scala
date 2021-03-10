package maven2sbt.core

import java.io.{File, InputStream}

import cats._
import cats.data._
import cats.syntax.all._

import effectie.cats.Effectful._
import effectie.cats._
import effectie.cats.EitherTSupport._

import scala.xml._

/**
  * @author Kevin Lee
  * @since 2017-04-03
  */
trait Maven2Sbt[F[_]] {
  def buildSbt(
    scalaVersion: ScalaVersion,
    propsName: Props.PropsName,
    scalaBinaryVersionName: Option[ScalaBinaryVersion.Name],
    pom: => Elem
  ): F[Either[Maven2SbtError, BuildSbt]]

  def buildSbtFromPomFile(
    scalaVersion: ScalaVersion,
    propsName: Props.PropsName,
    scalaBinaryVersionName: Option[ScalaBinaryVersion.Name],
    file: File
  ): F[Either[Maven2SbtError, BuildSbt]]

  def buildSbtFromInputStream(
    scalaVersion: ScalaVersion,
    propsName: Props.PropsName,
    scalaBinaryVersionName: Option[ScalaBinaryVersion.Name],
    pom: InputStream
  ): F[Either[Maven2SbtError, BuildSbt]]
}

object Maven2Sbt {

  def apply[F[_]: Monad: EffectConstructor: ConsoleEffect]: Maven2Sbt[F] = new Maven2SbtF[F]

  final class Maven2SbtF[F[_]: Monad: EffectConstructor: ConsoleEffect]
    extends Maven2Sbt[F] {

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def buildSbt(
      scalaVersion: ScalaVersion,
      propsName: Props.PropsName,
      scalaBinaryVersionName: Option[ScalaBinaryVersion.Name],
      pomElem: => Elem
    ): F[Either[Maven2SbtError, BuildSbt]] =
      for {
        pom <- effectOf(pomElem)
        projectInfo <- effectOf[F](ProjectInfo.from(pom))
        mavenProperties <- effectOf(MavenProperty.from(pom))
        props <- effectOf(mavenProperties.map(Prop.fromMavenProperty))
        libs <- effectOf(Libs.from(pom, scalaBinaryVersionName))
        repositories <- effectOf(Repository.from(pom))
        dependencies <- effectOf(Dependency.from(pom, scalaBinaryVersionName))
        globalSettings <- pureOf(BuildSbt.GlobalSettings.empty)
        thisBuildSettings <- pureOf(
            BuildSbt.ThisBuildSettings(BuildSbt.Settings(
                projectInfo.groupId.some
              , none[ArtifactId]
              , projectInfo.version.some
              , scalaVersion.some
              , List.empty[Repository]
              , List.empty[Dependency]
            ))
          )
        projectSettings <- pureOf(
            BuildSbt.ProjectSettings(BuildSbt.Settings(
                none[GroupId]
              , projectInfo.artifactId.some
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
            , libs
            )
          )
      } yield buildSbtData.asRight

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def buildSbtFromPomFile(
      scalaVersion: ScalaVersion,
      propsName: Props.PropsName,
      scalaBinaryVersionName: Option[ScalaBinaryVersion.Name],
      file: File
    ): F[Either[Maven2SbtError, BuildSbt]] =
      (for {
        pomFile <- eitherTOf(Option(file).filter(_.exists()).toRight(Maven2SbtError.pomFileNotExist(file)))
        pomElem <- eitherTRight(XML.loadFile(pomFile))
        buildSbtString <- EitherT(buildSbt(scalaVersion, propsName, scalaBinaryVersionName, pomElem))
      } yield buildSbtString).value

    @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
    def buildSbtFromInputStream(
      scalaVersion: ScalaVersion,
      propsName: Props.PropsName,
      scalaBinaryVersionName: Option[ScalaBinaryVersion.Name],
      pom: InputStream
    ): F[Either[Maven2SbtError, BuildSbt]] =
      (for {
        inputStream <- eitherTOf(Option(pom).toRight(Maven2SbtError.noPomInputStream))
        pomElem <- eitherTRight(XML.load(inputStream))
        buildSbtString <- EitherT(buildSbt(scalaVersion, propsName, scalaBinaryVersionName, pomElem))
      } yield buildSbtString).value

  }

}
