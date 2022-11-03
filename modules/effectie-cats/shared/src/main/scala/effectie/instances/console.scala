package effectie.instances

import cats.FlatMap
import cats.syntax.all._
import effectie.core.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.core.{ConsoleEffect, FxCtor, YesNo}

/** @author Kevin Lee
  * @since 2020-03-31
  */
object console {
  implicit def consoleEffectF[F[*]: FxCtor: FlatMap]: ConsoleEffect[F] =
    new ConsoleEffectWithoutFlatMap[F] with ConsoleEffect[F] {

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
