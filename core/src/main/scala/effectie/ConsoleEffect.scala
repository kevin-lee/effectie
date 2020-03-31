package effectie

trait ConsoleEffect[F[_]] {
  def readLn: F[String]

  def putStrLn(value: String): F[Unit]

  def putErrStrLn(value: String): F[Unit]

  def readYesNo(prompt: String): F[YesNo]
}

object ConsoleEffect {
  def apply[F[_] : ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]
}