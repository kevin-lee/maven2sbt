package maven2sbt.core

import refined4s.*

/** @author Kevin Lee
  * @since 2020-12-13
  */
object Props extends PropsPlus {

  type PropsName = PropsName.Type
  object PropsName extends Newtype[String]

}
