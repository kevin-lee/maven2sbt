package maven2sbt.core

import refined4s.*

import StringUtils.*

/** @author Kevin Lee
  * @since 2019-04-21
  */
final case class MavenProperty(key: MavenProperty.Name, value: MavenProperty.Value)

object MavenProperty extends MavenPropertyPlus {

  type Name = Name.Type
  object Name extends Newtype[String]

  type Value = Value.Type
  object Value extends Newtype[String]

}
