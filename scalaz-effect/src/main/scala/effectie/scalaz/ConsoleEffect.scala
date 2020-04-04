package effectie.scalaz

import effectie.YesNo

import scalaz._
import Scalaz._


trait ConsoleEffect[F[_]] extends effectie.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]

  implicit def consoleEffectF[F[_] : EffectConstructor : Bind]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

  final class ConsoleEffectF[F[_] : EffectConstructor : Bind] extends ConsoleEffect[F] {
    override def readLn: F[String] =
      EffectConstructor[F].effectOf(scala.io.StdIn.readLine)

    override def putStrLn(value: String): F[Unit] =
      EffectConstructor[F].effectOf(Console.out.println(value))

    override def putErrStrLn(value: String): F[Unit] =
      EffectConstructor[F].effectOf(Console.err.println(value))

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
    override def readYesNo(prompt: String): F[YesNo] = for {
      _ <- putStrLn(prompt)
      answer <- readLn
      yesOrN <-  answer match {
        case "y" | "Y" =>
          EffectConstructor[F].effectOf(YesNo.yes)
        case "n" | "N" =>
          EffectConstructor[F].effectOf(YesNo.no)
        case _ =>
          readYesNo(prompt)
      }
    } yield yesOrN

  }
}