package maven2sbt.core

import hedgehog._
import maven2sbt.core.Repository.{RepoId, RepoName, RepoUrl}

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object Gens {

  def genGroupId: Gen[GroupId] = Gen.string(Gen.alphaNum, Range.linear(1, 10)).map(GroupId)
  def genArtifactId: Gen[ArtifactId] = Gen.string(Gen.alphaNum, Range.linear(1, 10)).map(ArtifactId)
  def genVersion: Gen[Version] = Gen.string(Gen.alphaNum, Range.linear(1, 10)).map(Version)

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
  } yield Repository(id, name, url)

  def genMavenProperty: Gen[MavenProperty] = for {
    key <- Gen.string(Gen.alphaNum, Range.linear(1, 10))
    value <- Gen.string(Gen.unicode, Range.linear(1, 50))
  } yield MavenProperty(key, value)

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
