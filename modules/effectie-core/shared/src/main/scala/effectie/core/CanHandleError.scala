package effectie.core

import scala.annotation.implicitNotFound

/** @author Kevin Lee
  * @since 2020-08-17
  */
@implicitNotFound(
  """
  Could not find an implicit CanHandleError[${F}]. You can probably find it from the effectie.instances package.
  It is recommended to use an instance for Fx instead of an instance for CanHandleError.""" +
    compileTimeMessages.ListOfFxInstances
)
trait CanHandleError[F[*]] {

  def handleNonFatalWith[A, AA >: A](fa: => F[A])(handleError: Throwable => F[AA]): F[AA]

  @inline final def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: Throwable => F[Either[AA, BB]]
  ): F[Either[AA, BB]] =
    handleNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  def handleNonFatal[A, AA >: A](fa: => F[A])(handleError: Throwable => AA): F[AA]

  @inline final def handleEitherNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: Throwable => Either[AA, BB]
  ): F[Either[AA, BB]] =
    handleNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

}

object CanHandleError {

  def apply[F[*]: CanHandleError]: CanHandleError[F] = implicitly[CanHandleError[F]]

}
