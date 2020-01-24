package maven2sbt.effect

import cats.Id
import cats.effect.IO

/**
 * @author Kevin Lee
 * @since 2020-01-13
 */
trait Effect[F[_]] {
  def effect[A](a: => A): F[A]
}

object Effect {
  def apply[F[_] : Effect]: Effect[F] = implicitly[Effect[F]]

  implicit val ioEffect: Effect[IO] = new Effect[IO] {
    override def effect[A](a: => A): IO[A] = IO(a)
  }

  implicit val idNonEffect: Effect[Id] = new Effect[Id] {
    override def effect[A](a: => A): Id[A] = a
  }

}