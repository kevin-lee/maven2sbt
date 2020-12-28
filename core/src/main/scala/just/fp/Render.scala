package just.fp

import maven2sbt.core.Props

/**
 * @author Kevin Lee
 * @since 2020-03-14
 */
trait Render[A] {
  def render(propsName: Props.PropsName, a: A): String
}

object Render {
  def apply[A: Render]: Render[A] = implicitly[Render[A]]

  def namedRender[A](name: String, f: (Props.PropsName, A) => String): Render[A] =
    NamedRander(name, f)

  final case class NamedRander[A](name: String, f: (Props.PropsName, A) => String) extends Render[A] {
    override def render(propsName: Props.PropsName, a: A): String = f(propsName, a)
  }
}