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
trait Attempt[F[_]] extends effectie.Attempt[F] {
  type Xor[A, B] = Either[A, B]
}

object Attempt {
  def apply[F[_]: Attempt]: Attempt[F] = implicitly[Attempt[F]]

  implicit val attemptIo: Attempt[IO] = new Attempt[IO] {
    override def attempt[A, B](fb: IO[B])(f: Throwable => A): IO[Either[A, B]] =
      fb.attempt.map(_.leftMap(f))
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def attemptFuture(implicit EC: ExecutionContext): Attempt[Future] =
    new AttemptFuture(EC)

  final class AttemptFuture(val EC0: ExecutionContext)
    extends Attempt[Future] {
    @SuppressWarnings(Array("org.wartremover.warts.Nothing"))
    override def attempt[A, B](fb: Future[B])(f: Throwable => A): Future[Either[A, B]] =
      FutureCompat.transform(fb) {
        case scala.util.Success(b) =>
          scala.util.Try[Either[A, B]](Right(b))

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          scala.util.Try[Either[A, B]](Left(f(ex)))
      }(EC0)
  }

  implicit val attemptId: Attempt[Id] = new Attempt[Id] {
    override def attempt[A, B](fb: Id[B])(f: Throwable => A): Id[Either[A, B]] =
      scala.util.Try(fb) match {
        case scala.util.Success(b) =>
          b.asRight[A]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          f(ex).asLeft[B]
      }
  }

}