package maven2sbt.core

/**
 * @author Kevin Lee
 * @since 2020-12-13
 */
@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.ImplicitParameter"))
object Props extends PropsPlus {
  opaque type PropsName = String
  object PropsName {
    def apply(propsName: String): PropsName = propsName
    
    extension (propsName: PropsName) def value: String = propsName
    
  }
  
}
