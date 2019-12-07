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
  , property("test renderToResolvers with no resolver", testRenderToResolvers0)
  , property("test renderToResolvers with 1 resolver", testRenderToResolvers1)
  , property("test renderToResolvers with many resolvers", testRenderToResolversMany)
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

  def testRenderToResolvers0: Property = for {
    n <- Gen.int(Range.linear(0, 10)).log("n")
  } yield {
    val expected = ""
    val actual = Repository.renderToResolvers(List.empty, n)
    actual ==== expected
  }

  def testRenderToResolvers1: Property = for {
    n <- Gen.int(Range.linear(0, 10)).log("n")
    repository <- Gens.genRepository.log("repository")
  } yield {
    val expected = s"""resolvers += "${repository.name.repoName}" at "${repository.url.repoUrl}""""
    val actual = Repository.renderToResolvers(List(repository), n)
    actual ==== expected
  }

  def testRenderToResolversMany: Property = for {
    n <- Gen.int(Range.linear(0, 10)).log("n")
    repositories <- Gens.genRepository.list(Range.linear(2, 10)).log("repositories")
  } yield {
    val idt = " " * n
    val expected =
      s"""resolvers ++= Seq(
         |$idt${repositories.map(Repository.render).mkString("  ", s"\n$idt, ", "")}
         |$idt)""".stripMargin
    val actual = Repository.renderToResolvers(repositories, n)
    actual ==== expected
  }

}
