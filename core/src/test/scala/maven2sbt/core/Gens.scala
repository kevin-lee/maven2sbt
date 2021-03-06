package maven2sbt.core

import hedgehog._
import cats.syntax.all._
import maven2sbt.core.Repository.{RepoId, RepoName, RepoUrl}
import maven2sbt.core.{Prop => M2sProp}

import scala.util.Random

/**
 * @author Kevin Lee
 * @since 2019-04-22
 */
object Gens {

  def genCharByRange(range: List[(Int, Int)]): Gen[Char] =
    Gen.frequencyUnsafe(
      range.map { case (from, to) =>
        (to + 1 - from) -> Gen.char(from.toChar, to.toChar)
      }
    )


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
      .map(id => RepoId(id.stringsMkString("-")))

  def genRepositoryName: Gen[RepoName] =
    Gen.string(Gen.alphaNum, Range.linear(1, 10))
      .list(Range.linear(1, 5))
      .map(name => RepoName(name.stringsMkString(" ")))

  def genRepositoryUrl: Gen[RepoUrl] =
    Gen.string(Gen.alphaNum, Range.linear(1, 10))
      .list(Range.linear(1, 5))
      .map(url => RepoUrl(url.stringsMkString("https://", ".", "")))

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
    genRepositoryUrl.map(url => Repository(none[RepoId], none[RepoName], url))

  def genMavenPropertyNameWithPropNamePair: Gen[(MavenProperty.Name, Prop.PropName)] = for {
    nameList <- Gen.string(Gens.genCharByRange(TestUtils.ExpectedLetters), Range.linear(1, 10)).list(Range.linear(1, 10))
    delimiterList <- Gen.string(
        Gen.frequency1(70 -> Gen.element1('.', '-'), 30 -> Gens.genCharByRange(TestUtils.ExpectedNonLetters)),
        Range.singleton(1)
      )
      .list(Range.singleton(nameList.length - 1))
      .map(_.toVector)
    mavenPropName = nameList.zip(delimiterList :+ "").map { case (word, delimiter) => word + delimiter }.mkString

    propName = (
      nameList.headOption.map { first =>
        first.headOption.map { c =>
          if (c.isUpper || c.isLower || c === '_')
            c.toString
          else
            s"_${c.toString}"
        }.getOrElse("") + first.drop(1)
      }.toList ++ nameList.drop(1).map(_.capitalize)).mkString
  } yield (MavenProperty.Name(mavenPropName), Prop.PropName(propName))

  def genMavenProperty: Gen[MavenProperty] = for {
    nameList <- Gen.string(Gens.genCharByRange(TestUtils.ExpectedLetters), Range.linear(1, 10)).list(Range.linear(1, 10))

    delimiterList <- Gen.string(
        Gen.frequency1(70 -> Gen.element1('.', '-'), 30 -> Gens.genCharByRange(TestUtils.ExpectedNonLetters)),
        Range.singleton(1)
      )
      .list(Range.singleton(nameList.length - 1))
      .map(_.toVector)

    name = nameList.zip(delimiterList :+ "").map { case (word, delimiter) => word + delimiter }.mkString
    value <- Gen.string(Gen.unicode, Range.linear(1, 50))
  } yield MavenProperty(MavenProperty.Name(name), MavenProperty.Value(value))

  def genMavenPropertyAndPropPair: Gen[(MavenProperty, Prop)] = for {
    nameList <- Gen.string(Gens.genCharByRange(TestUtils.ExpectedLetters), Range.linear(1, 10)).list(Range.linear(1, 10))

    delimiterList <- Gen.string(
        Gen.frequency1(70 -> Gen.element1('.', '-'), 30 -> Gens.genCharByRange(TestUtils.ExpectedNonLetters)),
        Range.singleton(1)
      )
      .list(Range.singleton(nameList.length - 1))
      .map(_.toVector)

    key = nameList.zip(delimiterList :+ "").map { case (word, delimiter) => word + delimiter }.mkString

    expectedKey = (
      nameList.headOption.map { first =>
        first.headOption.map { c =>
          if (c.isUpper || c.isLower || c === '_')
            c.toString
          else
            s"_${c.toString}"
        }.getOrElse("") + first.drop(1)
      }.toList ++ nameList.drop(1).map(_.capitalize)).mkString

    value <- Gen.string(Gens.genCharByRange(TestUtils.NonWhitespaceCharRange), Range.linear(1, 50))
  } yield (
    MavenProperty(MavenProperty.Name(key), MavenProperty.Value(value)),
    Prop(Prop.PropName(expectedKey), Prop.PropValue(value))
  )

  def genRenderedStringFromValueWithPropsAndQuoted: Gen[(RenderedString, String)] = for {
    names <- Gens.genMavenPropertyNameWithPropNamePair
      .list(Range.linear(1, 5))

    values <- Gen
      .string(Gens.genCharByRange(TestUtils.NonWhitespaceCharRange), Range.linear(1, 10))
      .list(Range.singleton(names.length))

    nameValuePairs = names.zip(values)
    (_, valueWithExpectedProp) = Random
      .shuffle(nameValuePairs)
      .foldLeft(
        List.empty[((MavenProperty.Name, String), (M2sProp.PropName, String))]
      ) {
        case (acc, ((mavenPropName, propName), value)) =>
          ((mavenPropName, value), (propName, value)) :: acc

      }
      .unzip
    valueWithProps = valueWithExpectedProp
      .foldLeft(List.empty[String]) {
        case (acc, (prop, value)) => s"$${${prop.propName}}$value" :: acc
      }
      .reverse
      .mkString
  } yield (
    RenderedString.withProps(
      valueWithProps
    ),
    s"""s"$valueWithProps""""
  )

  def genRenderedStringWithOnlyPropNameAndQuoted: Gen[(RenderedString, String)] = for {
    (_, propName) <- Gens.genMavenPropertyNameWithPropNamePair
  } yield (
    RenderedString.withProps(
      s"$${${propName.propName}}"
    ),
    propName.propName
  )

  def genRenderedStringWithNoPropNameAndQuoted: Gen[(RenderedString, String)] =
    Gen.string(Gens.genCharByRange(TestUtils.NonWhitespaceCharRange), Range.linear(1, 10))
      .map(value => (RenderedString.withoutProps(value), s""""$value""""))

  def genRenderedStringWithQuotedString: Gen[(RenderedString, String)] =
    Gen.frequency1(
      30 -> genRenderedStringFromValueWithPropsAndQuoted,
      30 -> genRenderedStringWithOnlyPropNameAndQuoted,
      30 -> genRenderedStringWithNoPropNameAndQuoted
    )




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
    scalaLib <- Gen.boolean
  } yield if (scalaLib) {
      Dependency.scala(groupId, artifactId, version, scope, exclusions)
    } else {
      Dependency.java(groupId, artifactId, version, scope, exclusions)
    }

  def genLibValNameAndDependency: Gen[(Libs.LibValName, Dependency)] =
    for {
      dependency <- genDependency
    } yield
      (
        Libs.LibValName(
          StringUtils
            .capitalizeAfterIgnoringNonAlphaNumUnderscore(dependency.artifactId.artifactId)
        ),
        dependency
      )

}
