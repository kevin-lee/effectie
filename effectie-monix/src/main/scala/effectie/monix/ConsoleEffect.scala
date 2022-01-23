package effectie.monix

import cats._
import cats.syntax.all._
import effectie.core.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.core.YesNo

trait ConsoleEffect[F[_]] extends effectie.core.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] =
    implicitly[ConsoleEffect[F]]

  implicit def consoleEffectF[F[_]: effectie.core.FxCtor: FlatMap]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

  final class ConsoleEffectF[F[_]: effectie.core.FxCtor: FlatMap]
      extends ConsoleEffectWithoutFlatMap[F]
      with ConsoleEffect[F] {

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
    override def readYesNo(prompt: String): F[YesNo] = for {
      _      <- putStrLn(prompt)
      answer <- readLn
      yesOrN <- answer match {
                  case "y" | "Y" =>
                    effectie.core.FxCtor[F].effectOf(YesNo.yes)
                  case "n" | "N" =>
                    effectie.core.FxCtor[F].effectOf(YesNo.no)
                  case _ =>
                    readYesNo(prompt)
                }
    } yield yesOrN

  }
}
