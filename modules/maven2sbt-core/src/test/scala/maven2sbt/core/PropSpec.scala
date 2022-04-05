package maven2sbt.core

import hedgehog._
import hedgehog.runner._
import maven2sbt.core.{Prop => M2sProp}

object PropSpec extends Properties {

  override def tests: List[Test] = List(
    property("test Prop.fromMavenProperty", PropSpec.testFromMavenProperty),
    property("test Prop.render(prop)", PropSpec.testRender)
  )

  def testFromMavenProperty: Property =
    for {
      propsName <- Gen
        .string(
          Gens.genCharByRange(TestUtils.NonWhitespaceCharRange),
          Range.linear(1, 10)
        )
        .map(Props.PropsName.apply)
        .log("propsName")
      mavenProperties <- Gens.genMavenPropertyAndPropPair
        .list(Range.linear(0, 10))
        .log("mavenProperties")
    } yield {
      val (input, expected) = mavenProperties.map {
        case (mavenProperty, expectedProp) =>
          (mavenProperty, expectedProp)
      }.unzip
      val actual = input.map(mavenProperty => M2sProp.fromMavenProperty(mavenProperty))
      actual ==== expected
    }

  def testRender: Property = for {
    propsName <- Gen
      .string(
        Gens.genCharByRange(TestUtils.NonWhitespaceCharRange),
        Range.linear(1, 10)
      )
      .map(Props.PropsName.apply)
      .log("propsName")
    mavenProperty <- Gens.genMavenPropertyAndPropPair.log("mavenProperty")
  } yield {
    val (input, expected) =
      mavenProperty match {
        case (mavenProperty, expectedProp) =>
          (
            mavenProperty,
            RenderedString.noQuotesRequired(
              s"""val ${expectedProp.name.propName} = ${StringUtils.renderWithProps(propsName, expectedProp.value.propValue).toQuotedString}"""
            )
          )
      }
    val prop = M2sProp.fromMavenProperty(input)
    val actual = M2sProp.render(propsName, prop)
    actual ==== expected
  }

}