package maven2sbt.core

import maven2sbt.core.Props.PropsName
import maven2sbt.core.data.*

/** @author Kevin Lee
  * @since 2021-03-10
  */
trait PropsPlus {

  def renderProps(propsNmae: PropsName, indentSize: Int, props: List[Prop]): String = {
    val indent = StringUtils.indent(indentSize)
    props
      .map(Prop.render(propsNmae, _).toQuotedString)
      .stringsMkString(s"lazy val ${propsNmae.value} = new {\n$indent", s"\n$indent", s"\n}")
  }

}
