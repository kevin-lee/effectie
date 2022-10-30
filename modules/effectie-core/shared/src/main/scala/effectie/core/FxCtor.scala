package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit FxCtor[${F}]. You can probably find it from the effectie.instance package.
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2._

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3._

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3._

  For Scala's Future, It is just
    import effectie.instances.future.fxCtor._

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.fxCtor._
  ---
  """
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
