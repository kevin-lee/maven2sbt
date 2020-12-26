package maven2sbt.core

import hedgehog._
import hedgehog.runner._

import cats.syntax.all._

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
object CommonSpec extends Properties {
  override def tests: List[Test] = List(
    property("test indent", testIndent)
  , property("test dotSeparatedToCamelCase (a single name)", testDotSeparatedToCamelCase1)
  , property("test dotSeparatedToCamelCase (two or more names)", testDotSeparatedToCamelCaseMore)
  )

  def testIndent: Property = for {
    indentSize <- Gen.int(Range.linear(0, 50)).log("indentSize")
  } yield {
    val actual = Common.indent(indentSize)
    actual.length ==== indentSize and Result.assert(actual.trim.isEmpty)
  }

  def testDotSeparatedToCamelCase1: Property = for {
    name <- Gen.string(Gens.genCharByRange(TestUtils.ExpectedLetters), Range.linear(1, 10)).log("name")
  } yield {
    val expected = name.headOption.map { c =>
      if (c.isUpper || c.isLower || c === '_')
        c.toString
      else
        s"_${c.toString}"
    }.getOrElse("") + name.drop(1)
    val actual = Common.capitalizeAfterIgnoringNonAlphaNumUnderscore(name)
    actual ==== expected
  }
  def testDotSeparatedToCamelCaseMore: Property = for {
    (mavenPropName, propName) <- Gens.genMavenPropertyNameWithPropNamePair.log("(mavenPropName, propName)")
  } yield {
    val actual = Common.capitalizeAfterIgnoringNonAlphaNumUnderscore(mavenPropName.name)
    val expected = propName.propName
    actual ==== expected
  }
}
