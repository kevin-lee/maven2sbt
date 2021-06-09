package maven2sbt.core

import Repository._
import cats.Show
import cats.syntax.all._

import scala.xml.Elem

/** @author Kevin Lee
  * @since 2019-04-21
  */
final case class Repository(id: Option[RepoId], name: Option[RepoName], url: RepoUrl)

object Repository extends RepositoryPlus {
  opaque type RepoId = String
  object RepoId {
    def apply(repoId: String): RepoId = repoId

    def unapply(repoId: RepoId): Option[String] =
      repoId.value.some

    extension (repoId: RepoId) def value: String = repoId

    given show: Show[RepoId] = _.toString
  }

  opaque type RepoName = String
  object RepoName {
    def apply(repoName: String): RepoName = repoName

    def unapply(repoName: RepoName): Option[String] =
      repoName.value.some

    extension (repoName: RepoName) def value: String = repoName

    given show: Show[RepoName] = _.toString
  }

  opaque type RepoUrl = String
  object RepoUrl {
    def apply(repoUrl: String): RepoUrl = repoUrl

    extension (repoUrl: RepoUrl) def value: String = repoUrl
  }

}
