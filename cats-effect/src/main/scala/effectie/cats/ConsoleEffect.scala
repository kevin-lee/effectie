package effectie.cats

import cats._
import cats.implicits._

import effectie.YesNo

trait ConsoleEffect[F[_]] extends effectie.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]

  final class ConsoleEffectF[F[_] : EffectConstructor : Monad] extends ConsoleEffect[F] {
    override def readLn: F[String] =
      EffectConstructor[F].effect(scala.io.StdIn.readLine)

    override def putStrLn(value: String): F[Unit] =
      EffectConstructor[F].effect(Console.out.println(value))

    override def putErrStrLn(value: String): F[Unit] =
      EffectConstructor[F].effect(Console.err.println(value))

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
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