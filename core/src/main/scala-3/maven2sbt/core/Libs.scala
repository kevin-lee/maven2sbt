package maven2sbt.core

/**
 * @author Kevin Lee
 * @since 2021-03-04
 */
final case class Libs(dependencies: List[(Libs.LibValName, Dependency)])

object Libs extends LibsPlus {
  opaque type LibsName = String
  object LibsName {
    def apply(libsName: String): LibsName = libsName
    
    extension (libsName: LibsName) def value: String = libsName
  }
  
  opaque type LibValName = String
  object LibValName {
    def apply(libValName: String): LibValName = libValName
    
    extension (libValName: LibValName) def value: String = libValName
  }
  
}
