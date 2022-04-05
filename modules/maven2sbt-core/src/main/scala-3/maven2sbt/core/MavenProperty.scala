package maven2sbt.core

import scala.xml.Elem
import StringUtils._

/** @author Kevin Lee
  * @since 2019-04-21
  */
final case class MavenProperty(key: MavenProperty.Name, value: MavenProperty.Value)

object MavenProperty extends MavenPropertyPlus {

  type Name = Name.Name
  object Name {
    opaque type Name = String
    def apply(name: String): Name = name

    given nameCanEqual: CanEqual[Name, Name] = CanEqual.derived

    extension (name: Name) def value: String = name
  }

  type Value = Value.Value
  object Value {
    opaque type Value = String
    def apply(value: String): Value = value

    given valueCanEqual: CanEqual[Value, Value] = CanEqual.derived

    extension (value: Value) def value: String = value

  }

}
