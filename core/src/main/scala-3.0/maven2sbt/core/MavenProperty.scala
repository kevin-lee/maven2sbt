package maven2sbt.core

import scala.xml.Elem
import StringUtils._

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
final case class MavenProperty(key: MavenProperty.Name, value: MavenProperty.Value)

object MavenProperty extends MavenPropertyPlus {

  opaque type Name = String
  object Name {
    def apply(name: String): Name = name
    
    extension (name: Name) def value: String = name
  }
  
  opaque type Value = String
  object Value {
    def apply(value: String): Value = value
    
    extension (value: Value) def value: String = value
    
  }
  
}