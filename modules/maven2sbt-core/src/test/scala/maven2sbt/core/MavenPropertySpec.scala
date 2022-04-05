package maven2sbt.core

import hedgehog._
import hedgehog.runner._

import scala.xml.{Elem, Null, Text, TopScope}

/**
  * @author Kevin Lee
  * @since 2019-04-22
  */
object MavenPropertySpec extends Properties {

  @SuppressWarnings(Array("org.wartremover.warts.Any"))
  private def generatePom(properties: List[MavenProperty]): Elem =
    if (properties.isEmpty)
      <project></project>
    else
      <project>
        <properties>
          {
            properties.map { case MavenProperty(key, value)  =>
              Elem(null, key.value, Null, TopScope, true, Text(value.value))
            }
          }
        </properties>
      </project>


  override def tests: List[Test] = List(
    property("test from", testFrom)
  )

  def testFrom: Property = for {
    mavenProperties <- Gens.genMavenProperty.list(Range.linear(0, 10)).log("mavenProperties")
  } yield {
    val actual = MavenProperty.from(generatePom(mavenProperties))
    actual ==== mavenProperties
  }

}
