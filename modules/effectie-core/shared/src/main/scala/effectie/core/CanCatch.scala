package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit CanCatch[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.canCatch._
    // for Scala 3
    import effectie.instances.ce2.canCatch.*

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.canCatch._
    // for Scala 3
    import effectie.instances.ce3.canCatch.*

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.canCatch._
    // for Scala 3
    import effectie.instances.monix3.canCatch.*

  For Scala's Future, It is just
    import effectie.instances.future.canCatch._
    // for Scala 3
    import effectie.instances.future.canCatch.*

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.canCatch._
    // for Scala 3
    import effectie.instances.id.canCatch.*
  ---
  """
)
trait CanCatch[F[*]] {

  def mapFa[A, B](fa: F[A])(f: A => B): F[B]

  def catchNonFatalThrowable[A](fa: => F[A]): F[Either[Throwable, A]]

  @inline final def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Either[A, B]] =
    mapFa(catchNonFatalThrowable[B](fb))(ab => ab.left.map(f))

  @inline final def catchNonFatalEither[A, AA >: A, B](fab: => F[Either[A, B]])(f: Throwable => AA): F[Either[AA, B]] =
    mapFa(catchNonFatal(fab)(f))(_.joinRight)

}

object CanCatch {
  def apply[F[*]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

}
