package effectie.monix

import effectie.YesNo

trait ConsoleEffectful {

  def readLn[F[_]: ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  def readPassword[F[_]: ConsoleEffect]: F[Array[Char]] = ConsoleEffect[F].readPassword

  def putStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  def putErrStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  def readYesNo[F[_]: ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

}

object ConsoleEffectful extends ConsoleEffectful
