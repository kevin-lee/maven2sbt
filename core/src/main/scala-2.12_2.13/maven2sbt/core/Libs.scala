package maven2sbt.core


import io.estatico.newtype.macros.newtype

/**
 * @author Kevin Lee
 * @since 2021-03-04
 */
final case class Libs(dependencies: List[(Libs.LibValName, Dependency)])

object Libs extends LibsPlus {
  @newtype case class LibsName(value: String)
  
  @newtype case class LibValName(value: String)

}
