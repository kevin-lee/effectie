package effectie

trait ConsoleEffectful {

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.readLn or effectie.monix.ConsoleEffectful.readLn instead.",
    since = "1.4.0"
  )
  def readLn[F[_]: ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.putStrLn or effectie.monix.ConsoleEffectful.putStrLn instead.",
    since = "1.4.0"
  )
  def putStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.putErrStrLn or effectie.monix.ConsoleEffectful.putErrStrLn instead.",
    since = "1.4.0"
  )
  def putErrStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  @deprecated(
    message = "Use effectie.cats.ConsoleEffectful.readYesNo or effectie.monix.ConsoleEffectful.readYesNo instead.",
    since = "1.4.0"
  )
  def readYesNo[F[_]: ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

}

@deprecated(
  message = "Use effectie.cats.ConsoleEffectful or effectie.monix.ConsoleEffectful instead.",
  since = "1.4.0"
)
object ConsoleEffectful extends ConsoleEffectful
