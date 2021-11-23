package effectie.cats

import cats.Id
import cats.effect.IO
import cats.syntax.all._

//import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-06-07
  */
//trait CanCatch[F[_]] extends effectie.CanCatch[F] {
//
//  override type XorT[A, B] = EitherT[F, A, B]
//
//  @inline override final protected def xorT[A, B](fab: F[Either[A, B]]): EitherT[F, A, B] =
//    EitherT(fab)
//
//  @inline override final protected def xorT2FEither[A, B](efab: EitherT[F, A, B]): F[Either[A, B]] =
//    efab.value
//
//}

object CanCatch {
  type CanCatch[F[_]] = effectie.CanCatch[F]

  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  implicit object CanCatchIo extends CanCatch[IO] {

    @inline override final def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Either[Throwable, A]] =
      fa.attempt

  }

//  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
//  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] =
//    new effectie.CanCatch.CanCatchFuture with CanCatch[Future] {
//
//      override val EC0: ExecutionContext = EC
//
//    }

  implicit object CanCatchId extends CanCatch[Id] {

    @inline override final def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] = f(fa)

    override def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      scala.util.Try(fa) match {
        case scala.util.Success(a) =>
          a.asRight[Throwable]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          ex.asLeft[A]

        case scala.util.Failure(ex) =>
          throw ex
      }

  }

}
