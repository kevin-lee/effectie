package effectie.core

import scala.annotation.implicitNotFound

/** @author Kevin Lee
  * @since 2020-08-17
  */
@implicitNotFound(
  """
  Could not find an implicit CanHandleError[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.canHandleError._
    // for Scala 3
    import effectie.instances.ce2.canHandleError.given

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.canHandleError._
    // for Scala 3
    import effectie.instances.ce3.canHandleError.given

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.canHandleError._
    // for Scala 3
    import effectie.instances.monix3.canHandleError.given

  For Scala's Future, It is just
    import effectie.instances.future.canHandleError._
    // for Scala 3
    import effectie.instances.future.canHandleError.given

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.canHandleError._
    // for Scala 3
    import effectie.instances.id.canHandleError.given
  ---
  """
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
