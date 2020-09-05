package maven2sbt.core

import scala.xml.Elem
import Common._

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class MavenProperty(key: String, value: String)

object MavenProperty {
  def from(pom: Elem): Seq[MavenProperty] = for {
    properties <- pom \ "properties"
    property <- properties.child
    label = property.label
    if !label.startsWith("#PCDATA")
  } yield MavenProperty(label, property.text)

  // TODO: Remove it. It's no longer in use in favor of maven2sbt.core.BuildSbt.Prop.render.
  @deprecated(message = "Use maven2sbt.core.BuildSbt.Prop.render() instead", since = "1.0.0")
  def render(property: MavenProperty): String =
    s"""val ${dotHyphenSeparatedToCamelCase(property.key)} = "${property.value}""""

  def findPropertyName(name: String): Option[String] = name match {
    case propertyUsagePattern(value) => Some(value.trim)
    case _ => None
  }

  def toPropertyNameOrItself(name: String): String =
    findPropertyName(name).fold(s""""$name"""")(dotHyphenSeparatedToCamelCase)
}