package maven2sbt.core

import hedgehog._

import cats.syntax.all._
import maven2sbt.core.Repository.{RepoId, RepoName, RepoUrl}

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object Gens {

  final case class ExpectedMavenProperty(expectedMavenProperty: MavenProperty) extends AnyVal

  def genGroupId: Gen[GroupId] = Gen.string(Gen.alphaNum, Range.linear(1, 10)).map(GroupId.apply)
  def genArtifactId: Gen[ArtifactId] = Gen.string(Gen.alphaNum, Range.linear(1, 10)).map(ArtifactId.apply)
  def genVersion: Gen[Version] = Gen.string(Gen.alphaNum, Range.linear(1, 10)).map(Version.apply)

  def genProjectInfo: Gen[ProjectInfo] = for {
    groupId <- genGroupId
    artifactId <- genArtifactId
    version <- genVersion
  } yield ProjectInfo(groupId, artifactId, version)

  def genRepositoryId: Gen[RepoId] =
    Gen.string(Gen.alphaNum, Range.linear(1, 10))
      .list(Range.linear(1, 5))
      .map(id => RepoId(id.mkString("-")))

  def genRepositoryName: Gen[RepoName] =
    Gen.string(Gen.alphaNum, Range.linear(1, 10))
      .list(Range.linear(1, 5))
      .map(name => RepoName(name.mkString(" ")))

  def genRepositoryUrl: Gen[RepoUrl] =
    Gen.string(Gen.alphaNum, Range.linear(1, 10))
      .list(Range.linear(1, 5))
      .map(url => RepoUrl(url.mkString("https://", ".", "")))

  def genRepository: Gen[Repository] = for {
    id <- genRepositoryId
    name <- genRepositoryName
    url <- genRepositoryUrl
  } yield Repository(id.some, name.some, url)

  def genRepositoryWithEmptyName: Gen[Repository] = for {
    id <- genRepositoryId
    name = Repository.RepoName("")
    url <- genRepositoryUrl
  } yield Repository(id.some, name.some, url)

  def genRepositoryWithNoName: Gen[Repository] = for {
    id <- genRepositoryId
    url <- genRepositoryUrl
  } yield Repository(id.some, none[RepoName], url)

  def genRepositoryWithEmptyIdEmptyName: Gen[Repository] = for {
    url <- genRepositoryUrl
    id = Repository.RepoId("")
    name = Repository.RepoName("")
  } yield Repository(id.some, name.some, url)

  def genRepositoryWithNoIdNoName: Gen[Repository] =
    genRepositoryUrl.map (url => Repository(none[RepoId], none[RepoName], url))

  def genMavenProperty: Gen[MavenProperty] = for {
    keyFirst <- Gen.string(Gen.choice1(Gen.alpha, Gen.constant('_')), Range.singleton(1))
    keyHead <- Gen.string(Gen.choice1(Gen.alphaNum), Range.linear(1, 10))
        .list(Range.singleton(1)).map(keyFirst :: _)
    keyList <- Gen.string(Gen.frequency1(8 -> Gen.alphaNum, 2 -> Gen.constant('_')), Range.linear(0, 10))
        .list(Range.linear(3, 5)).map(keyHead ++ _)
    delimiterList <- Gen.string(Gen.element1('.', '-'), Range.singleton(1))
        .list(Range.singleton(keyList.length - 1))
    key = keyList.zip(delimiterList :+ "").map { case (word, delimiter) => word + delimiter }.mkString
    value <- Gen.string(Gen.unicode, Range.linear(1, 50))
  } yield MavenProperty(key, value)

  def genMavenPropertyWithExpectedRendered: Gen[(ExpectedMavenProperty, MavenProperty)] = for {
    keyFirst <- Gen.string(Gen.choice1(Gen.alpha, Gen.constant('_')), Range.singleton(1))
    keyHead <- Gen.string(Gen.choice1(Gen.alphaNum), Range.linear(1, 10))
        .list(Range.singleton(1)).map(keyFirst :: _)
    keyList <- Gen.string(Gen.frequency1(8 -> Gen.alphaNum, 2 -> Gen.constant('_')), Range.linear(0, 10))
        .list(Range.linear(3, 5)).map(keyHead ++ _)
    delimiterList <- Gen.string(Gen.element1('.', '-'), Range.singleton(1))
        .list(Range.singleton(keyList.length - 1))
    key = keyList.zip(delimiterList :+ "").map { case (word, delimiter) => word + delimiter }.mkString
    expectedKey = (keyList.headOption.toList ++ keyList.drop(1).map(_.capitalize)).mkString
    value <- Gen.string(Gen.unicode, Range.linear(1, 50))
  } yield (ExpectedMavenProperty(MavenProperty(expectedKey, value)), MavenProperty(key, value))

  def genScope: Gen[Scope] =
    Gen.element1(Scope.compile, Scope.test, Scope.provided, Scope.runtime, Scope.system, Scope.default)

  def genExclusion: Gen[Exclusion] = for {
    groupId <- genGroupId
    artifactId <- genArtifactId
  } yield Exclusion(groupId, artifactId)

  def genDependency: Gen[Dependency] = for {
    groupId <- genGroupId
    artifactId <- genArtifactId
    version <- genVersion
    scope <- genScope
    exclusions <- genExclusion.list(Range.linear(0, 5))
  } yield Dependency(groupId, artifactId, version, scope, exclusions)

}
