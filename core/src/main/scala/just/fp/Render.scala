package just.fp

/**
 * @author Kevin Lee
 * @since 2020-03-14
 */
trait Render[A] {
  def render(a: A): String
}

object Render {
  def apply[A : Render]: Render[A] = implicitly[Render[A]]

  def namedRender[A](name: String, f: A => String): Render[A] =
    NamedRander(name, f)

  final case class NamedRander[A](name: String, f: A => String) extends Render[A] {
    override def render(a: A): String = f(a)
  }
}