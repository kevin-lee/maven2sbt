package maven2sbt.core

import io.estatico.newtype.macros.newtype


/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class MavenProperty(key: MavenProperty.Name, value: MavenProperty.Value)

object MavenProperty extends MavenPropertyPlus {

  @newtype case class Name(value: String)
  @newtype case class Value(value: String)

}