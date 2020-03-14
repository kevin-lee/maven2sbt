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

  def render[A](f: A => String): Render[A] = new Render[A] {
    override def render(a: A): String = f(a)
  }
}