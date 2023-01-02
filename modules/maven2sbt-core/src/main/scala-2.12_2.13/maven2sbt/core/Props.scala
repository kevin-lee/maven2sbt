package maven2sbt.core

import io.estatico.newtype.macros.newtype

/** @author Kevin Lee
  * @since 2020-12-13
  */
@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.ImplicitParameter"))
object Props extends PropsPlus {
  @newtype case class PropsName(value: String)
}
