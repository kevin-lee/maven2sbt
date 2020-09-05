package maven2sbt.core

import hedgehog._
import hedgehog.runner._

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
    name <- Gen.string(Gen.alphaNum, Range.linear(1, 10)).log("name")
  } yield {
    val actual = Common.dotHyphenSeparatedToCamelCase(name)
    actual ==== name
  }
  def testDotSeparatedToCamelCaseMore: Property = for {
    names <- Gen.string(Gen.alphaNum, Range.linear(1, 10)).list(Range.linear(2, 5)).log("names")
  } yield {
    val dotSeparatedName = names.mkString(".")
    val expected = (names.head :: names.drop(1).map(_.capitalize)).mkString
    val actual = Common.dotHyphenSeparatedToCamelCase(dotSeparatedName)
    actual ==== expected
  }
}
