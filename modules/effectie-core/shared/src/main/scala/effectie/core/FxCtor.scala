package effectie.core

import effectie.core.FxCtor.{PureOfLeft, PureOfRight}

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit FxCtor[${F}]. You can probably find it from the effectie.instances package.
  It is recommended to use an instance for Fx instead of an instance for FxCtor.""" +
    compileTimeMessages.ListOfFxInstances
)
trait FxCtor[F[*]] {
  def effectOf[A](a: => A): F[A]

  def fromEffect[A](fa: => F[A]): F[A]

  def pureOf[A](a: A): F[A]
  def pureOrError[A](a: => A): F[A]

  def unitOf: F[Unit]

  def errorOf[A](throwable: Throwable): F[A]

  def fromEither[A](either: Either[Throwable, A]): F[A]
  def fromOption[A](option: Option[A])(orElse: => Throwable): F[A]
  def fromTry[A](tryA: scala.util.Try[A]): F[A]

  @inline def pureOfOption[A](a: A): F[Option[A]] = pureOf(Option(a))

  @inline def pureOfSome[A](a: A): F[Option[A]] = pureOf(Some(a))

  @inline def pureOfNone[A]: F[Option[A]] = pureOf(None)

  private val _pureOfRight: PureOfRight[F, Any] = new PureOfRight(this)
  @inline def pureOfRight[A]: PureOfRight[F, A] =
    _pureOfRight.asInstanceOf[PureOfRight[F, A]] // scalafix:ok DisableSyntax.asInstanceOf

  private val _pureOfLeft: PureOfLeft[F, Any] = new PureOfLeft(this)
  @inline def pureOfLeft[B]: PureOfLeft[F, B] =
    _pureOfLeft.asInstanceOf[PureOfLeft[F, B]] // scalafix:ok DisableSyntax.asInstanceOf

  def flatMapFa[A, B](fa: F[A])(f: A => F[B]): F[B]
}

object FxCtor {

  private[FxCtor] final class PureOfRight[F[*], A](
    private val F: FxCtor[F]
  ) extends AnyVal {
    def apply[B](b: B): F[Either[A, B]] = F.pureOf(Right(b))
  }

  private[FxCtor] final class PureOfLeft[F[*], B](
    private val F: FxCtor[F]
  ) extends AnyVal {
    def apply[A](a: A): F[Either[A, B]] = F.pureOf(Left(a))
  }

  def apply[F[*]: FxCtor]: FxCtor[F] = implicitly[FxCtor[F]]

}
