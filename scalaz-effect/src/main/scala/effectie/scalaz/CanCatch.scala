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
trait CanCatch[F[_]] extends effectie.CanCatch[F] {
  type Xor[A, B] = A \/ B
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  implicit val canCatchIo: CanCatch[IO] = new CanCatch[IO] {
    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def catchNonFatal[A, B](fb: IO[B])(f: Throwable => A): IO[A \/ B] =
      EitherT(fb.attempt)
        .leftMap {
          case NonFatal(ex) =>
            f(ex)
          case ex =>
            throw ex
        }.run

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] =
    new CanCatchFuture(EC)

  final class CanCatchFuture(val EC0: ExecutionContext)
    extends CanCatch[Future] {
    override def catchNonFatal[A, B](fb: Future[B])(f: Throwable => A): Future[A \/ B] =
      FutureCompat.transform(fb) {
        case SuccessS(b) =>
          Try(b.right[A])

        case FailureS(NonFatal(ex)) =>
          Try(f(ex).left[B])
      }(EC0)
  }

  implicit val canCatchId: CanCatch[Id] = new CanCatch[Id] {
    override def catchNonFatal[A, B](fb: Id[B])(f: Throwable => A): Id[A \/ B] =
      Try(fb) match {
        case SuccessS(b) =>
          b.right[A]

        case FailureS(NonFatal(ex)) =>
          f(ex).left[B]
      }
  }

}