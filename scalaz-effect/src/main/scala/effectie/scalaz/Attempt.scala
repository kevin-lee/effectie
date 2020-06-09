package effectie.scalaz

import scala.concurrent.{ExecutionContext, Future}
import scalaz._
import Scalaz._
import effectie.compat.FutureCompat
import scalaz.effect._

import scala.util.Try
import scala.util.{Failure => FailureS, Success => SuccessS}
import scala.util.control.NonFatal

/**
 * @author Kevin Lee
 * @since 2020-06-07
 */
trait Attempt[F[_]] extends effectie.Attempt[F] {
  type Xor[A, B] = A \/ B
}

object Attempt {
  def apply[F[_]: Attempt]: Attempt[F] = implicitly[Attempt[F]]

  implicit val attemptIo: Attempt[IO] = new Attempt[IO] {
    override def attempt[A, B](fb: IO[B])(f: Throwable => A): IO[A \/ B] =
      IO(Try(fb.map(Try(_) match {
        case SuccessS(b) =>
          b.right[A]

        case FailureS(NonFatal(ex)) =>
          f(ex).left[B]
      }))).flatMap {
        case SuccessS(b) =>
          b
        case FailureS(ex) =>
          IO(f(ex).left[B])
      }
  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def attemptFuture(implicit EC: ExecutionContext): Attempt[Future] =
    new AttemptFuture(EC)

  final class AttemptFuture(val EC0: ExecutionContext)
    extends Attempt[Future] {
    override def attempt[A, B](fb: Future[B])(f: Throwable => A): Future[A \/ B] =
      FutureCompat.transform(fb) {
        case SuccessS(b) =>
          Try(b.right[A])

        case FailureS(NonFatal(ex)) =>
          Try(f(ex).left[B])
      }(EC0)
  }

  implicit val attemptId: Attempt[Id] = new Attempt[Id] {
    override def attempt[A, B](fb: Id[B])(f: Throwable => A): Id[A \/ B] =
      Try(fb) match {
        case SuccessS(b) =>
          b.right[A]

        case FailureS(NonFatal(ex)) =>
          f(ex).left[B]
      }
  }

}