package maven2sbt.core

/** @author Kevin Lee
  * @since 2020-03-14
  */
trait Render[A] {
  def render(propsName: Props.PropsName, a: A): RenderedString
}

object Render {
  def apply[A: Render]: Render[A] = implicitly[Render[A]]

  def namedRender[A](
    name: String,
    f: (Props.PropsName, A) => RenderedString,
  ): Render[A] =
    NamedRender(name, f)

  final case class NamedRender[A](
    name: String,
    f: (Props.PropsName, A) => RenderedString,
  ) extends Render[A] {
    override def render(propsName: Props.PropsName, a: A): RenderedString =
      f(propsName, a)
  }
}
