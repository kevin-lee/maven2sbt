package maven2sbt.core

import Repository.*
import cats.Show
import cats.syntax.all.*

import refined4s.*
import refined4s.modules.cats.derivation.*

/** @author Kevin Lee
  * @since 2019-04-21
  */
final case class Repository(id: Option[RepoId], name: Option[RepoName], url: RepoUrl)

object Repository extends RepositoryPlus {

  type RepoId = RepoId.Type
  object RepoId extends Newtype[String], CatsEqShow[String]

  type RepoName = RepoName.Type
  object RepoName extends Newtype[String], CatsEqShow[String]

  type RepoUrl = RepoUrl.Type
  object RepoUrl extends Newtype[String], CatsEqShow[String]

}
