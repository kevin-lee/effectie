package effectie.core

import scala.annotation.implicitNotFound

/** @author Kevin Lee
  * @since 2020-08-17
  */
@implicitNotFound(
  """
  Could not find an implicit CanRecover[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.canRecover._
    // for Scala 3
    import effectie.instances.ce2.canRecover.given

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.canRecover._
    // for Scala 3
    import effectie.instances.ce3.canRecover.given

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.canRecover._
    // for Scala 3
    import effectie.instances.monix3.canRecover.given

  For Scala's Future, It is just
    import effectie.instances.future.canRecover._
    // for Scala 3
    import effectie.instances.future.canRecover.given

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.canRecover._
    // for Scala 3
    import effectie.instances.id.canRecover.given
  ---
  """
)
trait CanRecover[F[*]] {

  def recoverFromNonFatalWith[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, F[AA]]): F[AA]

  @inline final def recoverEitherFromNonFatalWith[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, F[Either[AA, BB]]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

  def recoverFromNonFatal[A, AA >: A](fa: => F[A])(handleError: PartialFunction[Throwable, AA]): F[AA]

  @inline final def recoverEitherFromNonFatal[A, AA >: A, B, BB >: B](
    fab: => F[Either[A, B]]
  )(
    handleError: PartialFunction[Throwable, Either[AA, BB]]
  ): F[Either[AA, BB]] =
    recoverFromNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

}

object CanRecover {

  def apply[F[*]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

}
