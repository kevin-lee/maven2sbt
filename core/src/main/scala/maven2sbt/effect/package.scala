package maven2sbt

import cats.Monad

/**
 * @author Kevin Lee
 * @since 2019-12-31
 */
package object effect {

  def readLnF[F[_]: Monad]: F[String] = Monad[F].pure(scala.io.StdIn.readLine)
  def putStrLnF[F[_]: Monad](value: String): F[Unit] = Monad[F].pure(Console.out.println(value))
  def putErrStrLnF[F[_]: Monad](value: String): F[Unit] = Monad[F].pure(Console.err.println(value))

}
