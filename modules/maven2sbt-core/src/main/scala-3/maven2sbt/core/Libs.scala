package maven2sbt.core

import refined4s.*

/** @author Kevin Lee
  * @since 2021-03-04
  */
final case class Libs(dependencies: List[(Libs.LibValName, Dependency)])

object Libs extends LibsPlus {

  type LibsName = LibsName.Type
  object LibsName extends Newtype[String]

  type LibValName = LibValName.Type
  object LibValName extends Newtype[String]

}
