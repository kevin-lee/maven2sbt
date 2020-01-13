package maven2sbt.effect

import cats.effect.IO

/**
 * @author Kevin Lee
 * @since 2020-01-13
 */
object CatsIo {

  implicit val catsIo: Lazy[IO] = new Lazy[IO] {
    override def fOf[A](a: => A): IO[A] = IO(a)
  }

}
