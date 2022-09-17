package effectie.syntax

import cats._
import cats.syntax.all._
import console.ConsoleEffectF
import effectie.core.ConsoleEffect.ConsoleEffectWithoutFlatMap
import effectie.core.{ConsoleEffect, FxCtor, YesNo}

trait console {

  @inline def readLn[F[*]: ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  @inline def readPassword[F[*]: ConsoleEffect]: F[Array[Char]] = ConsoleEffect[F].readPassword

  @inline def putStr[F[*]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStr(value)

  @inline def putStrLn[F[*]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  @inline def putErrStr[F[*]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStr(value)

  @inline def putErrStrLn[F[*]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  @inline def readYesNo[F[*]: ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

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
