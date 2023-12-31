package maven2sbt.core

import maven2sbt.core.data.*
final case class Prop(name: Prop.PropName, value: Prop.PropValue)

object Prop {
  final case class PropName(propName: String) extends AnyVal
  final case class PropValue(propValue: String) extends AnyVal

  def fromMavenProperty(mavenProperty: MavenProperty): Prop =
    Prop(
      PropName(StringUtils.capitalizeAfterIgnoringNonAlphaNumUnderscore(mavenProperty.key.value)),
      PropValue(mavenProperty.value.value),
    )

  def render(propsName: Props.PropsName, prop: Prop): RenderedString = {
    val propValue = StringUtils.renderWithProps(propsName, prop.value.propValue).toQuotedString
    RenderedString.noQuotesRequired(
      s"""val ${prop.name.propName} = $propValue""",
    )
  }

  implicit val propRender: Render[Prop] =
    Render.namedRender("prop", (propsName, prop) => Prop.render(propsName, prop))

}
