package effectie.cats

import cats._
import cats.syntax.all._
import effectie.cats.console.ConsoleEffectF
import effectie.core.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.core.{ConsoleEffect, FxCtor, YesNo}

trait console {

  implicit def consoleEffectF[F[_]: FxCtor: FlatMap]: ConsoleEffect[F] =
    new ConsoleEffectF[F]

}

object console extends console {

  final class ConsoleEffectF[F[_]: FxCtor: FlatMap] extends ConsoleEffectWithoutFlatMap[F] with ConsoleEffect[F] {

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
