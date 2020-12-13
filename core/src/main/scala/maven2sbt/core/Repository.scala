package maven2sbt.core

import Common._

import scala.xml.Elem

import Repository._

import cats.syntax.all._
import just.fp.{Named, Render}

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

  // TODO: Remove it. It's no longer in use in favor of maven2sbt.core.BuildSbt.toListOfFieldValue.
  @deprecated(message = "Use maven2sbt.core.BuildSbt.toListOfFieldValue() instead", since = "1.0.0")
  def renderToResolvers(
    repositories: Seq[Repository],
    indentSize: Int
  ): String = {
    val idt = indent(indentSize)
    repositories match {
      case Nil =>
        ""

      case x :: Nil =>
        s"""resolvers += ${render(x)}"""

      case x :: xs =>
        s"""resolvers ++= Seq(
           |$idt  ${render(x)},
           |${xs.map(render).mkString(s"$idt  ", s",\n$idt  ", "")}
           |$idt)""".stripMargin
    }
  }

}