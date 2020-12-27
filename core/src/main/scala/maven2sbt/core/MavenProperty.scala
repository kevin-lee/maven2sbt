package maven2sbt.core

import scala.xml.Elem
import StringUtils._
import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class MavenProperty(key: MavenProperty.Name, value: MavenProperty.Value)

object MavenProperty {

  @newtype case class Name(name: String)
  @newtype case class Value(value: String)

  def from(pom: Elem): Seq[MavenProperty] = for {
    properties <- pom \ "properties"
    property <- properties.child
    label = property.label
    if !label.startsWith("#PCDATA")
  } yield MavenProperty(Name(label), Value(property.text))

  def findPropertyName(name: String): Option[String] = name match {
    case propertyUsagePattern(value) => Some(value.trim)
    case _ => None
  }

}