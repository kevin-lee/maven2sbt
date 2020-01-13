package maven2sbt

/**
 * @author Kevin Lee
 * @since 2019-12-31
 */
package object effect {

  def readLnF[F[_] : Lazy]: F[String] =
    Lazy[F].fOf(scala.io.StdIn.readLine)

  def putStrLnF[F[_] : Lazy](value: String): F[Unit] =
    Lazy[F].fOf(Console.out.println(value))

  def putErrStrLnF[F[_] : Lazy](value: String): F[Unit] =
    Lazy[F].fOf(Console.err.println(value))

}
