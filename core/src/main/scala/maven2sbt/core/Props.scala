package maven2sbt.core

import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions

/**
 * @author Kevin Lee
 * @since 2020-12-13
 */
object Props {
  @newtype case class PropsName(propsName: String)

  def renderProps(propsNmae: PropsName, indentSize: Int, props: List[Prop]): String = {
    val indent = " " * indentSize
    props.map { prop =>
      s"""val ${prop.name.propName} = "${prop.value.propValue}""""
    }.mkString(s"lazy val ${propsNmae.propsName} = new {\n$indent", s"\n$indent", s"\n}")
  }

}

final case class Prop(name: Prop.PropName, value: Prop.PropValue)

object Prop {
  final case class PropName(propName: String) extends AnyVal
  final case class PropValue(propValue: String) extends AnyVal

  def fromMavenProperty(mavenProperty: MavenProperty): Prop =
    Prop(
      PropName(Common.dotHyphenSeparatedToCamelCase(mavenProperty.key)),
      PropValue(mavenProperty.value)
    )

  def render(prop: Prop): String =
    s"""val ${prop.name.propName} = "${prop.value.propValue}""""

}
