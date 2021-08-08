package effectie.scalaz

import effectie.YesNo
import scalaz._
import Scalaz._
import effectie.ConsoleEffect.ConsoleEffectWithoutFlatMap

trait ConsoleEffect[F[_]] extends effectie.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] =
    implicitly[ConsoleEffect[F]]

  implicit def consoleEffectF[F[_]: Fx: Bind]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

  final class ConsoleEffectF[F[_]: Fx: Bind] extends ConsoleEffectWithoutFlatMap[F] with ConsoleEffect[F] {

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
    override def readYesNo(prompt: String): F[YesNo] = for {
      _      <- putStrLn(prompt)
      answer <- readLn
      yesOrN <- answer match {
                  case "y" | "Y" =>
                    Fx[F].effectOf(YesNo.yes)
                  case "n" | "N" =>
                    Fx[F].effectOf(YesNo.no)
                  case _         =>
                    readYesNo(prompt)
                }
    } yield yesOrN

  }
}
