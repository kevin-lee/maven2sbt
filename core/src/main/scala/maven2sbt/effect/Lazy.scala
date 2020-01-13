package maven2sbt.effect

/**
 * @author Kevin Lee
 * @since 2020-01-13
 */
trait Lazy[F[_]] {
  def fOf[A](a: => A): F[A]
}

object Lazy {
  def apply[F[_] : Lazy]: Lazy[F] = implicitly[Lazy[F]]
}