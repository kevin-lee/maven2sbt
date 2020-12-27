package maven2sbt.core

import hedgehog._
import hedgehog.runner._
import maven2sbt.core.{Prop => M2sProp}

object PropSpec extends Properties {

  override def tests: List[Test] = List(
    property("test Prop.fromMavenProperty", PropSpec.testFromMavenProperty),
    property("test Prop.render(prop)", PropSpec.testRender)
  )

  def testFromMavenProperty: Property = for {
      mavenProperties <- Gens.genMavenPropertyAndPropPair.list(Range.linear(0, 10))
          .log("mavenProperties")
    } yield {
      val (input, expected) = mavenProperties.map {
        case (mavenProperty, expectedProp) =>
          (
            mavenProperty,
            expectedProp
          )
      }.unzip
      val actual = input.map(M2sProp.fromMavenProperty)
      actual ==== expected
    }

  def testRender: Property = for {
    mavenProperty <- Gens.genMavenPropertyAndPropPair.log("mavenProperty")
  } yield {
    val (input, expected) =
      mavenProperty match {
        case (mavenProperty, expectedProp) =>
          (
            mavenProperty,
            s"""val ${expectedProp.name.propName} = "${expectedProp.value.propValue}""""
          )
      }
    val prop = M2sProp.fromMavenProperty(input)
    val actual = M2sProp.render(prop)
    actual ==== expected
  }

}