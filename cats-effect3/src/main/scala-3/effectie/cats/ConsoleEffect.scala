package effectie.cats

import cats.*
import cats.syntax.all.*
import effectie.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.{FxCtor, YesNo}

trait ConsoleEffect[F[*]] extends effectie.ConsoleEffect[F]

object ConsoleEffect {
  def apply[F[*]: ConsoleEffect]: ConsoleEffect[F] =
    summon[ConsoleEffect[F]]

  given consoleEffectF[F[*]: FxCtor: FlatMap]: ConsoleEffect[F] = new ConsoleEffectF[F]

  final class ConsoleEffectF[F[*]: FxCtor: FlatMap] extends ConsoleEffectWithoutFlatMap[F] with ConsoleEffect[F] {

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
