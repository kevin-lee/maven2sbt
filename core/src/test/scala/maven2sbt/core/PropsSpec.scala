package maven2sbt.core

import hedgehog._
import hedgehog.runner._
import maven2sbt.core.{Prop => M2sProp}

/**
 * @author Kevin Lee
 * @since 2020-12-13
 */
object PropsSpec extends Properties {
  override def tests: List[Test] = List(
    property("test Props.render", testPropsRender)
  )

  def testPropsRender: Property = for {
    mavenProperties <- Gens.genMavenPropertyAndPropPair
        .list(Range.linear(1, 10))
        .log("mavenProperties")
    indentSize <- Gen.int(Range.linear(0, 8)).log("indent")
  } yield {
    val propsName = Props.PropsName("testProps")
    val (input, propsRendered) = mavenProperties.map {
      case (mavenProperty, expectedProp) =>
        (
          mavenProperty,
          s"""val ${expectedProp.name.propName} = ${StringUtils.renderWithProps(propsName, expectedProp.value.propValue).toQuotedString}"""
        )
    }.unzip
    val indent = " " * indentSize
    val expected = propsRendered.stringsMkString(s"lazy val ${propsName.propsName} = new {\n$indent", s"\n$indent", "\n}")
    val props = input.map(mavenProperty => M2sProp.fromMavenProperty(mavenProperty))
    val actual = Props.renderProps(propsName, indentSize, props)
    actual ==== expected
  }
}
