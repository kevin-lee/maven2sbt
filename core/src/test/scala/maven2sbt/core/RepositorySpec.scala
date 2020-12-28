package maven2sbt.core

import cats.syntax.all._
import hedgehog._
import hedgehog.runner._

import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object RepositorySpec extends Properties {

  override def tests: List[Test] = List(
    property("test Repository.from(xml)", testRepositoryFromXml)
  , property("test Repository.from(xml) with empty RepoName", testRepositoryFromXmlWithEmptyRepoName)
  , property("test Repository.from(xml) with empty RepoId and empty RepoName", testRepositoryFromXmlWithEmptyRepoIdEmptyRepoName)
  , property("test Repository.from(xml) with no RepoName", testRepositoryFromXmlWithNoRepoName)
  , property("test Repository.from(xml) with no RepoId and no RepoName", testRepositoryFromXmlWithNoRepoIdNoRepoName)
  , property("test Repository.render(Repository)", testRenderRepository)
  , property("test Repository.render(Repository) with empty RepoName", testRenderRepositoryWithEmptyName)
  , property("test Repository.render(Repository) with no RepoName", testRenderRepositoryWithNoName)
  , property("test Repository.render(Repository) with empty id and empty RepoName", testRenderRepositoryWithEmptyIdEmptyName)
  , property("test Repository.render(Repository) with no id and no RepoName", testRenderRepositoryWithNoIdNoName)
  )

  def testRepositoryFromXml: Property = for {
    repositories <- Gens.genRepository.list(Range.linear(0, 10)).log("repositories")
  } yield {
    val pom = generatePom(repositories)
    val actual = Repository.from(pom)
    actual ==== repositories
  }

  def testRepositoryFromXmlWithEmptyRepoName: Property = for {
    repositories <- Gens.genRepositoryWithEmptyName.list(Range.linear(0, 10)).log("repositories")
  } yield {
    val pom = generatePom(repositories)
    val actual = Repository.from(pom)
    actual ==== repositories
  }

  def testRepositoryFromXmlWithEmptyRepoIdEmptyRepoName: Property = for {
    repositories <- Gens.genRepositoryWithEmptyIdEmptyName.list(Range.linear(0, 10)).log("repositories")
  } yield {
    val pom = generatePom(repositories)
    val actual = Repository.from(pom)
    actual ==== repositories
  }

  def testRepositoryFromXmlWithNoRepoName: Property = for {
    repositories <- Gens.genRepositoryWithNoName.list(Range.linear(0, 10)).log("repositories")
  } yield {
    val pom = generatePom(repositories)
    val actual = Repository.from(pom)
    actual ==== repositories
  }

  def testRepositoryFromXmlWithNoRepoIdNoRepoName: Property = for {
    repositories <- Gens.genRepositoryWithNoIdNoName.list(Range.linear(0, 10)).log("repositories")
  } yield {
    val pom = generatePom(repositories)
    val actual = Repository.from(pom)
    actual ==== repositories
  }

  def testRenderRepository: Property = for {
    repository <- Gens.genRepository.log("repository")
  } yield {
    repository.name match {
      case Some(repoName) =>
        val propsName = Props.PropsName("testProps")
        val expected = RenderedString.noQuotesRequired(s""""${repoName.repoName}" at "${repository.url.repoUrl}"""")
        val actual = Repository.render(propsName, repository)
        actual ==== expected
      case None =>
        Result.failure.log(s"RepoName in the Repository generated by Gens.genRepository is None. repository: ${repository.show}")
    }
  }

  def testRenderRepositoryWithEmptyName: Property = for {
    repository <- Gens.genRepositoryWithEmptyName.log("repository")
  } yield {
    repository.id match {
      case Some(repoId) =>
        val propsName = Props.PropsName("testProps")
        val expected = RenderedString.noQuotesRequired(s""""${repoId.repoId}" at "${repository.url.repoUrl}"""")
        val actual = Repository.render(propsName, repository)
        actual ==== expected
      case None =>
        Result.failure.log(s"RepoId in the Repository generated by Gens.genRepository is None. repository: ${repository.show}")
    }
  }

  def testRenderRepositoryWithNoName: Property = for {
    repository <- Gens.genRepositoryWithNoName.log("repository")
  } yield {
    repository.id match {
      case Some(repoId) =>
        val propsName = Props.PropsName("testProps")
        val expected = RenderedString.noQuotesRequired(s""""${repoId.repoId}" at "${repository.url.repoUrl}"""")
        val actual = Repository.render(propsName, repository)
        actual ==== expected
      case None =>
        Result.failure.log(s"RepoId in the Repository generated by Gens.genRepository is None. repository: ${repository.show}")
    }
  }

  def testRenderRepositoryWithEmptyIdEmptyName: Property = for {
    repository <- Gens.genRepositoryWithEmptyIdEmptyName.log("repository")
  } yield {
    val propsName = Props.PropsName("testProps")
    val expected = RenderedString.noQuotesRequired(s""""${repository.url.repoUrl}" at "${repository.url.repoUrl}"""")
    val actual = Repository.render(propsName, repository)
    actual ==== expected
  }

  def testRenderRepositoryWithNoIdNoName: Property = for {
    repository <- Gens.genRepositoryWithNoIdNoName.log("repository")
  } yield {
    val propsName = Props.PropsName("testProps")
    val expected = RenderedString.noQuotesRequired(s""""${repository.url.repoUrl}" at "${repository.url.repoUrl}"""")
    val actual = Repository.render(propsName, repository)
    actual ==== expected
  }



  private def genRepo(
    repoId: Option[Repository.RepoId],
    maybeRepoName: Option[Repository.RepoName],
    repoUrl: Repository.RepoUrl
  ): Elem =
    (repoId, maybeRepoName) match {
      case (Some(repoId), Some(repoName)) =>
        <repository>
          <id>{ repoId.repoId }</id>
          <name>{ repoName.repoName }</name>
          <url>{ repoUrl.repoUrl }</url>
        </repository>

      case (Some(repoId), None) =>
        <repository>
          <id>{ repoId.repoId }</id>
          <url>{ repoUrl.repoUrl }</url>
        </repository>

      case (None, Some(repoName)) =>
        <repository>
          <name>{ repoName.repoName }</name>
          <url>{ repoUrl.repoUrl }</url>
        </repository>

      case (None, None) =>
        <repository>
          <url>{ repoUrl.repoUrl }</url>
        </repository>
    }


  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def generatePom(repos: List[Repository]): Elem =
    if (repos.isEmpty)
      <project></project>
    else
      <project>
        <repositories>
          {
            repos.map { case Repository(id, name, url)  =>
              genRepo(id, name, url)
            }
          }
        </repositories>
      </project>

}
