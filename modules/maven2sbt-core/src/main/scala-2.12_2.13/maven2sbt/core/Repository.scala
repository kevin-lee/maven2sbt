package maven2sbt.core

import cats.Show
import cats.syntax.all.*
import io.estatico.newtype.macros.newtype


/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class Repository(id: Option[Repository.RepoId], name: Option[Repository.RepoName], url: Repository.RepoUrl)

object Repository extends RepositoryPlus {
  @newtype case class RepoId(value: String)
  object RepoId {
    def unapply(repoId: RepoId): Option[String] =
      repoId.value.some

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    implicit final val show: Show[RepoId] = _.toString
  }
  @newtype case class RepoName(value: String)
  object RepoName {
    def unapply(repoName: RepoName): Option[String] =
      repoName.value.some

    @SuppressWarnings(Array("org.wartremover.warts.ToString"))
    implicit final val show: Show[RepoName] = _.toString
  }
  @newtype case class RepoUrl(value: String)

}