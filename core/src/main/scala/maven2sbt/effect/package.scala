package maven2sbt

/**
 * @author Kevin Lee
 * @since 2019-12-31
 */
package object effect {

  def readLnF[F[_] : Effect]: F[String] =
    Effect[F].effect(scala.io.StdIn.readLine)

  def putStrLnF[F[_] : Effect](value: String): F[Unit] =
    Effect[F].effect(Console.out.println(value))

  def putErrStrLnF[F[_] : Effect](value: String): F[Unit] =
    Effect[F].effect(Console.err.println(value))

}
