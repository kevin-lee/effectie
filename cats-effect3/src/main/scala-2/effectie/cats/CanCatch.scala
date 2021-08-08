package effectie.cats

import cats.Id
import cats.data.EitherT
import cats.effect.IO
import cats.syntax.all._

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2020-06-07
  */
trait CanCatch[F[_]] extends effectie.CanCatch[F] {
  override type Xor[A, B]  = Either[A, B]
  override type XorT[A, B] = EitherT[F, A, B]

  override def catchNonFatalEitherT[A, B](fab: => EitherT[F, A, B])(f: Throwable => A): EitherT[F, A, B] =
    EitherT(catchNonFatalEither(fab.value)(f))
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  implicit val canCatchIo: CanCatch[IO] = new CanCatch[IO] {
    override def catchNonFatal[A, B](fb: => IO[B])(f: Throwable => A): IO[Either[A, B]] =
      fb.attempt.map(_.leftMap(f))

    override def catchNonFatalEither[A, B](fab: => IO[Either[A, B]])(f: Throwable => A): IO[Either[A, B]] =
      catchNonFatal(fab)(f).map(_.joinRight)
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] =
    new CanCatchFuture(EC)

  final class CanCatchFuture(val EC0: ExecutionContext) extends CanCatch[Future] {
    @SuppressWarnings(Array("org.wartremover.warts.Nothing", "org.wartremover.warts.Throw"))
    override def catchNonFatal[A, B](fb: => Future[B])(f: Throwable => A): Future[Either[A, B]] =
      fb.transform {
        case scala.util.Success(b) =>
          scala.util.Try[Either[A, B]](Right(b))

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          scala.util.Try[Either[A, B]](Left(f(ex)))

        case scala.util.Failure(ex) =>
          throw ex
      }(EC0)

    override def catchNonFatalEither[A, B](fab: => Future[Either[A, B]])(f: Throwable => A): Future[Either[A, B]] =
      catchNonFatal(fab)(f).map(_.joinRight)(EC0)

  }

  implicit val canCatchId: CanCatch[Id] = new CanCatch[Id] {
    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def catchNonFatal[A, B](fb: => Id[B])(f: Throwable => A): Id[Either[A, B]] =
      scala.util.Try(fb) match {
        case scala.util.Success(b) =>
          b.asRight[A]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          f(ex).asLeft[B]

        case scala.util.Failure(ex) =>
          throw ex
      }

    override def catchNonFatalEither[A, B](fab: => Id[Either[A, B]])(f: Throwable => A): Id[Either[A, B]] =
      catchNonFatal(fab)(f).joinRight

  }

}
