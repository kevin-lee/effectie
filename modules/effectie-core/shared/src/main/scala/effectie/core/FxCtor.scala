package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit FxCtor[${F}]. You can probably find it from the effectie.instances package.
  It is recommended to use an instance for Fx instead of an instance for FxCtor.""" +
    compileTimeMessages.ListOfFxInstances
)
trait FxCtor[F[*]] {
  def effectOf[A](a: => A): F[A]
  def pureOf[A](a: A): F[A]
  def pureOrError[A](a: => A): F[A]

  def unitOf: F[Unit]

  def errorOf[A](throwable: Throwable): F[A]

  def fromEither[A](either: Either[Throwable, A]): F[A]
  def fromOption[A](option: Option[A])(orElse: => Throwable): F[A]
  def fromTry[A](tryA: scala.util.Try[A]): F[A]
}

object FxCtor {

  def apply[F[*]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

}
