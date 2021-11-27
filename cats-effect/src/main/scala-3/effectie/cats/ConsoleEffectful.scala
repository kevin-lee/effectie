package effectie.cats

import effectie.YesNo

trait ConsoleEffectful {

  def readLn[F[*]: ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  def readPassword[F[*]: ConsoleEffect]: F[Array[Char]] = ConsoleEffect[F].readPassword

  def putStrLn[F[*]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  def putErrStrLn[F[*]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  def readYesNo[F[*]: ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

}

object ConsoleEffectful extends ConsoleEffectful
