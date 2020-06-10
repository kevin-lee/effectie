package effectie.cats

import cats.Id
import cats.effect.IO
import cats.implicits._
import effectie.compat.FutureCompat

import scala.concurrent.{ExecutionContext, Future}

/**
 * @author Kevin Lee
 * @since 2020-06-07
 */
trait CanCatch[F[_]] extends effectie.CanCatch[F] {
  type Xor[A, B] = Either[A, B]
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  implicit val canCatchIo: CanCatch[IO] = new CanCatch[IO] {
    override def catchNonFatal[A, B](fb: IO[B])(f: Throwable => A): IO[Either[A, B]] =
      fb.attempt.map(_.leftMap(f))
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] =
    new CanCatchFuture(EC)

  final class CanCatchFuture(val EC0: ExecutionContext)
    extends CanCatch[Future] {
    @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
    override def catchNonFatal[A, B](fb: Future[B])(f: Throwable => A): Future[Either[A, B]] =
      FutureCompat.transform(fb) {
        case scala.util.Success(b) =>
          scala.util.Try[Either[A, B]](Right(b))

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          scala.util.Try[Either[A, B]](Left(f(ex)))
      }(EC0)
  }

  implicit val canCatchId: CanCatch[Id] = new CanCatch[Id] {
    override def catchNonFatal[A, B](fb: Id[B])(f: Throwable => A): Id[Either[A, B]] =
      scala.util.Try(fb) match {
        case scala.util.Success(b) =>
          b.asRight[A]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          f(ex).asLeft[B]
      }
  }

}