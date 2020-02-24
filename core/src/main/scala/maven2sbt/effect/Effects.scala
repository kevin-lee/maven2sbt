package maven2sbt.effect

import cats._
import cats.implicits._
import cats.effect._

/**
 * @author Kevin Lee
 * @since 2020-01-13
 */
trait EffectConstructor[F[_]] {
  def effect[A](a: => A): F[A]
  def pureEffect[A](a: A): F[A]
  def unit: F[Unit]
}

object EffectConstructor {
  def apply[F[_] : EffectConstructor]: EffectConstructor[F] = implicitly[EffectConstructor[F]]

  implicit val ioEffectConstructor: EffectConstructor[IO] = new EffectConstructor[IO] {

    override def effect[A](a: => A): IO[A] = IO(a)

    override def pureEffect[A](a: A): IO[A] = IO.pure(a)

    override def unit: IO[Unit] = IO.unit
  }

  implicit val idSideEffectConstructor: EffectConstructor[Id] = new EffectConstructor[Id] {

    override def effect[A](a: => A): Id[A] = a

    override def pureEffect[A](a: A): Id[A] = a

    override def unit: Id[Unit] = ()
  }

}

trait ConsoleEffect[F[_]] {
  def readLn: F[String]

  def putStrLn(value: String): F[Unit]

  def putErrStrLn(value: String): F[Unit]

  def readYesNo(prompt: String): F[YesNo]
}

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]

  implicit val ioConsoleEffect: ConsoleEffect[IO] = new ConsoleEffectF[IO]

  final class ConsoleEffectF[F[_] : EffectConstructor : Monad] extends ConsoleEffect[F] {
    override def readLn: F[String] =
      EffectConstructor[F].effect(scala.io.StdIn.readLine)

    override def putStrLn(value: String): F[Unit] =
      EffectConstructor[F].effect(Console.out.println(value))

    override def putErrStrLn(value: String): F[Unit] =
      EffectConstructor[F].effect(Console.err.println(value))

    override def readYesNo(prompt: String): F[YesNo] = for {
      _ <- putStrLn(prompt)
      answer <- readLn
      yesOrN <-  answer match {
        case "y" | "Y" =>
          EffectConstructor[F].effect(YesNo.yes)
        case "n" | "N" =>
          EffectConstructor[F].effect(YesNo.no)
        case _ =>
          readYesNo(prompt)
      }
    } yield yesOrN

  }
}

trait Effectful[F[_]] {

  implicit protected def EF: EffectConstructor[F]

  def effect[A](a: => A): F[A] = EF.effect(a)

  def pureEffect[A](a: A): F[A] = EF.pureEffect(a)

  def effectUnit: F[Unit] = EF.unit

}

trait ConsoleEffectful[F[_]] {

  implicit protected def CF: ConsoleEffect[F]

  def readLn: F[String] = CF.readLn

  def putStrLn(value: String): F[Unit] = CF.putStrLn(value)

  def putErrStrLn(value: String): F[Unit] = CF.putErrStrLn(value)

  def readYesNo(prompt: String): F[YesNo] = CF.readYesNo(prompt)

}
