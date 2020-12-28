package maven2sbt.core

import Repository._
import cats.Show
import cats.syntax.all._
import io.estatico.newtype.macros.newtype
import just.fp.{Named, Render}

import scala.xml.Elem

import scala.language.implicitConversions

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class Repository(id: Option[RepoId], name: Option[RepoName], url: RepoUrl)

object Repository {
  @newtype case class RepoId(repoId: String)
  object RepoId {
    def unapply(repoId: RepoId): Option[String] =
      repoId.repoId.some

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    implicit final val show: Show[RepoId] = _.toString
  }
  @newtype case class RepoName(repoName: String)
  object RepoName {
    def unapply(repoName: RepoName): Option[String] =
      repoName.repoName.some

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    implicit final val show: Show[RepoName] = _.toString
  }
  @newtype case class RepoUrl(repoUrl: String)

  implicit val namedRepository: Named[Repository] = Named.named("resolvers")

  implicit val renderRepository: Render[Repository] =
    Render.namedRender("repository", (propsName, repository) => Repository.render(propsName, repository))

  def from(pom: Elem): Seq[Repository] = for {
    repositories <- pom \ "repositories"
    repository <- repositories.child
    url = (repository \ "url").text
    if url.nonEmpty
    idSeq = (repository \ "id")
    repoId =
      if (idSeq.isEmpty)
        none[RepoId]
      else
        Option(idSeq.text)
          .map(id => RepoId(id.trim))
    nameSeq = (repository \ "name")
    repoName =
      if (nameSeq.isEmpty)
        none[RepoName]
      else
        Option(nameSeq.text)
          .map(name => RepoName(name.trim))
  } yield Repository(repoId, repoName, RepoUrl(url))

  def render(propsName: Props.PropsName, repository: Repository): RenderedString = {
    val repoUrlStr = StringUtils.renderWithProps(propsName, repository.url.repoUrl)
    val repoNameStr = (repository.id.filter(_.repoId.nonEmpty), repository.name.filter(_.repoName.nonEmpty)) match {
        case (_, Some(repoName)) =>
          repoName.repoName
        case (Some(repoId), None) =>
          repoId.repoId
        case (None, None) =>
          repoUrlStr.innerValue
      }
    RenderedString.noQuotesRequired(
      s""""$repoNameStr" at ${repoUrlStr.toQuotedString}"""
    )
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  implicit final val show: Show[Repository] = _.toString
}