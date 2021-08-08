package effectie.cats

import cats.*
import cats.syntax.all.*
import effectie.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.YesNo

trait ConsoleEffect[F[_]] extends effectie.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] = summon[ConsoleEffect[F]]

  given consoleEffectF[F[_]: FxCtor: FlatMap]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

  final class ConsoleEffectF[F[_]: FxCtor: FlatMap] extends ConsoleEffectWithoutFlatMap[F] with ConsoleEffect[F] {

    override def readYesNo(prompt: String): F[YesNo] = for {
      _      <- putStrLn(prompt)
      answer <- readLn
      yesOrN <- answer match {
                  case "y" | "Y" =>
                    FxCtor[F].effectOf(YesNo.yes)
                  case "n" | "N" =>
                    FxCtor[F].effectOf(YesNo.no)
                  case _         =>
                    readYesNo(prompt)
                }
    } yield yesOrN

  }
}
