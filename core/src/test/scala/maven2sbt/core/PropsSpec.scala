package maven2sbt.core

import hedgehog._
import hedgehog.runner._
import maven2sbt.core.Gens.ExpectedMavenProperty
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
    mavenProperties <- Gens.genMavenPropertyWithExpectedRendered
        .list(Range.linear(1, 10))
        .log("mavenProperties")
    indentSize <- Gen.int(Range.linear(0, 8)).log("indent")
  } yield {
    val (propsRendered, props) = mavenProperties.map {
      case (ExpectedMavenProperty(MavenProperty(expectedKey, expectedValue)), mavenProperty) =>
        (
          s"""val $expectedKey = "${expectedValue}"""",
          M2sProp.fromMavenProperty(mavenProperty)
        )
    }.unzip
    val propsName = Props.PropsName("testProps")
    val indent = " " * indentSize
    val expected = propsRendered.mkString(s"lazy val $propsName = new {\n$indent", s"\n$indent", "\n}")
    val actual = Props.renderProps(propsName, indentSize, props)
    actual ==== expected
  }
}
