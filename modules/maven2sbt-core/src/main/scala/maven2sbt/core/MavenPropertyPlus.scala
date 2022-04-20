package maven2sbt.core

import maven2sbt.core.MavenProperty.{Name, Value}
import StringUtils._

import scala.xml.Elem

/** @author Kevin Lee
  * @since 2021-03-11
  */
trait MavenPropertyPlus {

  def from(pom: Elem): Seq[MavenProperty] = for {
    properties <- pom \ "properties"
    property   <- properties.child
    label = property.label
    if !label.startsWith("#PCDATA")
  } yield MavenProperty(Name(label), Value(property.text))

  def findPropertyName(name: String): Option[String] = name match {
    case propertyUsagePattern(value) => Some(value.trim)
    case _ => None
  }

}
