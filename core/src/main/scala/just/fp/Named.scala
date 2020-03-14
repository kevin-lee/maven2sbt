package just.fp

/**
 * @author Kevin Lee
 * @since 2020-03-14
 */
trait Named[A] {
  def name: String
}

object Named {
  def apply[A : Named]: Named[A] = implicitly[Named[A]]

  def named[A](namedName: String): Named[A] = new Named[A] {
    override def name: String = namedName
  }
}