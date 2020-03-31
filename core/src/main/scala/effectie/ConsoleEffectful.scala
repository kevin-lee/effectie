package effectie

trait ConsoleEffectful {

  def readLn[F[_] : ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  def putStrLn[F[_] : ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  def putErrStrLn[F[_] : ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  def readYesNo[F[_] : ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

}
