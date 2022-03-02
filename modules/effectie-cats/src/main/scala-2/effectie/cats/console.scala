package effectie.cats

import cats._
import cats.syntax.all._
import effectie.cats.console.ConsoleEffectF
import effectie.core.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.core.{ConsoleEffect, FxCtor, YesNo}

trait console {

  implicit def consoleEffectF[F[*]: FxCtor: FlatMap]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

}

object console extends console {

  final class ConsoleEffectF[F[*]: FxCtor: FlatMap] extends ConsoleEffectWithoutFlatMap[F] with ConsoleEffect[F] {

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
    override def readYesNo(prompt: String): F[YesNo] = for {
      _      <- putStrLn(prompt)
      answer <- readLn
      yesOrN <- answer match {
                  case "y" | "Y" =>
                    FxCtor[F].pureOf(YesNo.yes)
                  case "n" | "N" =>
                    FxCtor[F].pureOf(YesNo.no)
                  case _ =>
                    readYesNo(prompt)
                }
    } yield yesOrN

  }
}
