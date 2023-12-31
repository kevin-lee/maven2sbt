package maven2sbt.core

import cats.Show
import cats.syntax.all.none
import just.fp.Named
import maven2sbt.core.Repository.{RepoId, RepoName, RepoUrl}
import maven2sbt.core.data.*

import scala.xml.Elem

/** @author Kevin Lee
  * @since 2021-03-11
  */
trait RepositoryPlus {

  implicit val namedRepository: Named[Repository] = Named.named("resolvers")

  implicit val renderRepository: Render[Repository] =
    Render.namedRender("repository", (propsName, repository) => Repository.render(propsName, repository))

  def from(pom: Elem): Seq[Repository] = for {
    repositories <- pom \ "repositories"
    repository   <- repositories.child
    url      = (repository \ "url").text
    if url.nonEmpty
    idSeq    = (repository \ "id")
    repoId   =
      if (idSeq.isEmpty)
        none[RepoId]
      else
        Option(idSeq.text)
          .map(id => RepoId(id.trim))
    nameSeq  = (repository \ "name")
    repoName =
      if (nameSeq.isEmpty)
        none[RepoName]
      else
        Option(nameSeq.text)
          .map(name => RepoName(name.trim))
  } yield Repository(repoId, repoName, RepoUrl(url))

  def render(propsName: Props.PropsName, repository: Repository): RenderedString = {
    val repoUrlStr  = StringUtils.renderWithProps(propsName, repository.url.value)
    val repoNameStr = (repository.id.filter(_.value.nonEmpty), repository.name.filter(_.value.nonEmpty)) match {
      case (_, Some(repoName)) =>
        repoName.value
      case (Some(repoId), None) =>
        repoId.value
      case (None, None) =>
        repoUrlStr.innerValue
    }
    RenderedString.noQuotesRequired(
      s""""$repoNameStr" at ${repoUrlStr.toQuotedString}""",
    )
  }

  @SuppressWarnings(Array("org.wartremover.warts.ToString"))
  implicit val repositoryShow: Show[Repository] = _.toString
}
