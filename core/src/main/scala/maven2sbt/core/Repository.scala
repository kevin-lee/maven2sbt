package maven2sbt.core

import Repository._
import cats.syntax.all._
import just.fp.{Named, Render}

import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class Repository(id: Option[RepoId], name: Option[RepoName], url: RepoUrl)

object Repository {
  final case class RepoId(repoId: String) extends AnyVal
  final case class RepoName(repoName: String) extends AnyVal
  final case class RepoUrl(repoUrl: String) extends AnyVal

  implicit val named: Named[Repository] = Named.named("resolvers")

  implicit val render: Render[Repository] =
    Render.namedRender("repository", repository => Repository.render(repository))

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

  def render(repository: Repository): String = {
    val repoUrlStr = repository.url.repoUrl
    val repoNameStr = (repository.id.filter(_.repoId.nonEmpty), repository.name.filter(_.repoName.nonEmpty)) match {
        case (_, Some(repoName)) =>
          repoName.repoName
        case (Some(repoId), None) =>
          repoId.repoId
        case (None, None) =>
          repoUrlStr
      }
    s""""$repoNameStr" at "$repoUrlStr""""
  }

}