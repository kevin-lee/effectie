package effectie

trait ConsoleEffectful {

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.readLn or effectie.scalaz.ConsoleEffectful.readLn instead.",
    since = "1.4.0"
  )
  def readLn[F[_]: ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.putStrLn or effectie.scalaz.ConsoleEffectful.putStrLn instead.",
    since = "1.4.0"
  )
  def putStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.putErrStrLn or effectie.scalaz.ConsoleEffectful.putErrStrLn instead.",
    since = "1.4.0"
  )
  def putErrStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.readYesNo or effectie.scalaz.ConsoleEffectful.readYesNo instead.",
    since = "1.4.0"
  )
  def readYesNo[F[_]: ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

}

@deprecated(
  message = "Use effectie.cats.ConsoleEffectful or effectie.scalaz.ConsoleEffectful instead.",
  since = "1.4.0"
)
object ConsoleEffectful extends ConsoleEffectful
