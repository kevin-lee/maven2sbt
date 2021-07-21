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

  type RepoId = RepoId.RepoId
  object RepoId {
    opaque type RepoId = String
    def apply(repoId: String): RepoId = repoId

    def unapply(repoId: RepoId): Option[String] =
      repoId.value.some

    given repoIdCanEqual: CanEqual[RepoId, RepoId] = CanEqual.derived

    extension (repoId: RepoId) def value: String = repoId

    given show: Show[RepoId] = _.toString
  }

  type RepoName = RepoName.RepoName
  object RepoName {
    opaque type RepoName = String
    def apply(repoName: String): RepoName = repoName

    given repoNameCanEqual: CanEqual[RepoName, RepoName] = CanEqual.derived

    def unapply(repoName: RepoName): Option[String] =
      repoName.value.some

    extension (repoName: RepoName) def value: String = repoName

    given show: Show[RepoName] = _.toString
  }

  type RepoUrl = RepoUrl.RepoUrl
  object RepoUrl {
    opaque type RepoUrl = String
    def apply(repoUrl: String): RepoUrl = repoUrl

    given repoUrlCanEqual: CanEqual[RepoUrl, RepoUrl] = CanEqual.derived

    extension (repoUrl: RepoUrl) def value: String = repoUrl
  }

}
