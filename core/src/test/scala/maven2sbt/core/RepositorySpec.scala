package maven2sbt.core

import hedgehog._
import hedgehog.runner._

import scala.xml.Elem

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object RepositorySpec extends Properties {

  private def generatePom(repos: List[Repository]): Elem =
    if (repos.isEmpty)
      <project></project>
    else
      <project>
        <repositories>
          {
            repos.map { case Repository(id, name, url)  =>
              <repository>
                <id>{ id.repoId }</id>
                <name>{ name.repoName }</name>
                <url>{ url.repoUrl }</url>
              </repository>
            }
          }
        </repositories>
      </project>

  override def tests: List[Test] = List(
    property("test from", testFrom)
  , property("test render", testRender)
  )

  def testFrom: Property = for {
    repositories <- Gens.genRepository.list(Range.linear(0, 10)).log("repositories")
  } yield {
    val pom = generatePom(repositories)
    val actual = Repository.from(pom)
    actual ==== repositories
  }

  def testRender: Property = for {
    repository <- Gens.genRepository.log("repository")
  } yield {
    val expected = s""""${repository.name.repoName}" at "${repository.url.repoUrl}""""
    val actual = Repository.render(repository)
    actual ==== expected
  }

}
