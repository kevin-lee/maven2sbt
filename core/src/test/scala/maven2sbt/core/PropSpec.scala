package maven2sbt.core

import hedgehog._
import hedgehog.runner._
import maven2sbt.core.Gens.ExpectedMavenProperty
import maven2sbt.core.{Prop => M2sProp}

object PropSpec extends Properties {

  override def tests: List[Test] = List(
    property("test Prop.fromMavenProperty", PropSpec.testFromMavenProperty),
    property("test Prop.render(prop)", PropSpec.testRender)
  )

  def testFromMavenProperty: Property = for {
      mavenProperties <- Gens.genMavenPropertyWithExpectedRendered.list(Range.linear(0, 10))
          .log("mavenProperties")
    } yield {
      val (expected, actual) = mavenProperties.map {
        case (ExpectedMavenProperty(expectedMavenProperty), mavenProperty) =>
          (
            M2sProp(
              M2sProp.PropName(expectedMavenProperty.key),
              M2sProp.PropValue(expectedMavenProperty.value)
            ),
            M2sProp.fromMavenProperty(mavenProperty)
          )
      }.unzip
      actual ==== expected
    }

  def testRender: Property = for {
    mavenProperty <- Gens.genMavenPropertyWithExpectedRendered.log("mavenProperty")
  } yield {
    val (expected, prop) =
      mavenProperty match {
        case (ExpectedMavenProperty(expectedMavenProperty), mavenProperty) =>
          (
            s"""val ${expectedMavenProperty.key} = "${expectedMavenProperty.value}"""",
            M2sProp.fromMavenProperty(mavenProperty)
          )
      }
    val actual = M2sProp.render(prop)
    actual ==== expected
  }

}