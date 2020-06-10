package effectie.scalaz

import scalaz._
import Scalaz._

import effectie.ConsoleEffect.ConsoleEffectWithoutBind
import effectie.YesNo


trait ConsoleEffect[F[_]] extends effectie.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]

  implicit def consoleEffectF[F[_]: EffectConstructor: Bind]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

  final class ConsoleEffectF[F[_]: EffectConstructor: Bind]
    extends ConsoleEffectWithoutBind[F]
    with ConsoleEffect[F] {

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