package maven2sbt.core

/** @author Kevin Lee
  * @since 2020-12-13
  */
object Props extends PropsPlus {

  type PropsName = PropsName.PropsName
  object PropsName {
    opaque type PropsName = String
    def apply(propsName: String): PropsName = propsName

    given propsNameCanEqual: CanEqual[PropsName, PropsName] = CanEqual.derived

    extension (propsName: PropsName) def value: String = propsName
  }

}
