package maven2sbt.core

/** @author Kevin Lee
  * @since 2021-03-04
  */
final case class Libs(dependencies: List[(Libs.LibValName, Dependency)])

object Libs extends LibsPlus {

  type LibsName = LibsName.LibsName
  object LibsName {
    opaque type LibsName = String
    def apply(libsName: String): LibsName = libsName

    given libsNameCanEqual: CanEqual[LibsName, LibsName] = CanEqual.derived

    extension (libsName: LibsName) def value: String = libsName
  }

  type LibValName = LibValName.LibValName
  object LibValName {
    opaque type LibValName = String
    def apply(libValName: String): LibValName = libValName

    given libValNameCanEqual: CanEqual[LibValName, LibValName] = CanEqual.derived

    extension (libValName: LibValName) def value: String = libValName
  }

}
