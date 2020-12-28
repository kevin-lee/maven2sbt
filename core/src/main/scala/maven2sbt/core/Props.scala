package maven2sbt.core

import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions

/**
 * @author Kevin Lee
 * @since 2020-12-13
 */
@SuppressWarnings(Array("org.wartremover.warts.ImplicitConversion", "org.wartremover.warts.ImplicitParameter"))
object Props {
  @newtype case class PropsName(propsName: String)

  def renderProps(propsNmae: PropsName, indentSize: Int, props: List[Prop]): String = {
    val indent = StringUtils.indent(indentSize)
    props.map(Prop.render)
      .mkString(s"lazy val ${propsNmae.propsName} = new {\n$indent", s"\n$indent", s"\n}")
  }

}

final case class Prop(name: Prop.PropName, value: Prop.PropValue)

object Prop {
  final case class PropName(propName: String) extends AnyVal
  final case class PropValue(propValue: RenderedString) extends AnyVal

  def fromMavenProperty(propsName: Props.PropsName, mavenProperty: MavenProperty): Prop =
    Prop(
      PropName(StringUtils.capitalizeAfterIgnoringNonAlphaNumUnderscore(mavenProperty.key.name)),
      PropValue(StringUtils.renderWithProps(propsName, mavenProperty.value.value))
    )

  def render(prop: Prop): String = {
    val propValue = prop.value.propValue.toQuotedString
    s"""val ${prop.name.propName} = $propValue"""
  }

}
