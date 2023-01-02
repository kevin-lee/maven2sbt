package maven2sbt.core

import hedgehog.*
import hedgehog.runner.*
import cats.syntax.all.*

import scala.util.Random

import maven2sbt.core.Prop as M2sProp

/** @author Kevin Lee
  * @since 2019-04-21
  */
object StringUtilsSpec extends Properties {
  override def tests: List[Test] = List(
    property("test indent", testIndent),
    property(
      "test capitalizeAfterIgnoringNonAlphaNumUnderscore (a single name)",
      testCapitalizeAfterIgnoringNonAlphaNumUnderscore1,
    ),
    property(
      "test capitalizeAfterIgnoringNonAlphaNumUnderscore (two or more names)",
      testCapitalizeAfterIgnoringNonAlphaNumUnderscoreMore,
    ),
    property("test renderWithProps", testRenderWithProps),
    property("test quoteRenderedString", testQuoteRenderedString),
  )

  def testIndent: Property =
    for {
      indentSize <- Gen.int(Range.linear(0, 50)).log("indentSize")
    } yield {
      val actual = StringUtils.indent(indentSize)
      actual.length ==== indentSize and Result.assert(actual.trim.isEmpty)
    }

  def testCapitalizeAfterIgnoringNonAlphaNumUnderscore1: Property =
    for {
      name <- Gen
                .string(
                  Gens.genCharByRange(TestUtils.ExpectedLetters),
                  Range.linear(1, 10),
                )
                .log("name")
    } yield {
      val expected = name
        .headOption
        .map { c =>
          if (c.isUpper || c.isLower || c === '_')
            c.toString
          else
            s"_${c.toString}"
        }
        .getOrElse("") + name.drop(1)
      val actual   =
        StringUtils.capitalizeAfterIgnoringNonAlphaNumUnderscore(name)
      actual ==== expected
    }

  def testCapitalizeAfterIgnoringNonAlphaNumUnderscoreMore: Property =
    for {
      mavenPropNameAndPropName <- Gens
                                    .genMavenPropertyNameWithPropNamePair
                                    .log("(mavenPropName, propName)")
    } yield {
      val (mavenPropName, propName) = mavenPropNameAndPropName
      val actual                    = StringUtils.capitalizeAfterIgnoringNonAlphaNumUnderscore(
        mavenPropName.value,
      )
      val expected                  = propName.propName
      actual ==== expected
    }

  def testRenderWithProps: Property =
    for {
      propsNameHead <- Gen.string(Gen.choice1(Gen.alpha, Gen.constant('_')), Range.singleton(1)).log("propsNameHead")
      propsName     <- Gen
                         .string(Gen.choice1(Gen.alphaNum, Gen.constant('_')), Range.linear(1, 10))
                         .map(name => s"$propsNameHead$name")
                         .map(Props.PropsName.apply)
                         .log("propsName")
      names         <- Gens
                         .genMavenPropertyNameWithPropNamePair
                         .list(Range.linear(1, 5))
                         .log("propNames")
      values        <- Gen
                         .string(Gens.genCharByRange(TestUtils.NonWhitespaceCharRange), Range.linear(1, 10))
                         .list(Range.singleton(names.length))
                         .log("values")
      nameValuePairs                           = names.zip(values)
      (valuesWithProps, valueWithExpectedProp) =
        Random
          .shuffle(nameValuePairs)
          .foldLeft(
            List.empty[((MavenProperty.Name, String), (M2sProp.PropName, String))],
          ) {
            case (acc, ((mavenPropName, propName), value)) =>
              ((mavenPropName, value), (propName, value)) :: acc
          }
          .unzip
    } yield {
      val input = valuesWithProps
        .foldLeft(List.empty[String]) {
          case (acc, (prop, value)) => s"$${${prop.value}}$value" :: acc
        }
        .reverse
        .mkString

      val expected = RenderedString.withProps(
        valueWithExpectedProp
          .foldLeft(List.empty[String]) {
            case (acc, (prop, value)) => s"$${${propsName.value}.${prop.propName}}$value" :: acc
          }
          .reverse
          .mkString,
      )

      val actual = StringUtils.renderWithProps(propsName, input)
      actual ==== expected
    }

  def testQuoteRenderedString: Property = for {
    renderedStringQuoted <- Gens.genRenderedStringWithQuotedString.log("(renderedString, quoted)")
  } yield {
    val (renderedString, quoted) = renderedStringQuoted
    val actual                   = StringUtils.quoteRenderedString(renderedString)
    actual ==== quoted
  }

}
