package effectie.scalaz

import scalaz.{Scalaz, _}
import Scalaz._
import scalaz.effect._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
import scala.util.{Try, Failure => FailureS, Success => SuccessS}

/** @author Kevin Lee
  * @since 2020-06-07
  */
trait CanCatch[F[_]] extends effectie.CanCatch[F] {
  override type Xor[A, B]  = A \/ B
  override type XorT[A, B] = EitherT[F, A, B]

  override def catchNonFatalEitherT[A, B](fab: => EitherT[F, A, B])(f: Throwable => A): EitherT[F, A, B] =
    EitherT(catchNonFatalEither(fab.run)(f))
}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  implicit val canCatchIo: CanCatch[IO] = new CanCatch[IO] {

    override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Throwable \/ A] =
      fa.attempt
        .map(_.leftMap {
          case NonFatal(ex) =>
            ex
          case ex           =>
            throw ex
        })

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def catchNonFatal[A, B](fb: => IO[B])(f: Throwable => A): IO[A \/ B] =
      fb.attempt
        .map(_.leftMap {
          case NonFatal(ex) =>
            f(ex)
          case ex           =>
            throw ex
        })

    override def catchNonFatalEither[A, B](fab: => IO[A \/ B])(f: Throwable => A): IO[A \/ B] =
      catchNonFatal(fab)(f).map(_.flatMap(identity))

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] =
    new CanCatchFuture(EC)

  final class CanCatchFuture(val EC0: ExecutionContext) extends CanCatch[Future] {

    override def catchNonFatalThrowable[A](fa: => Future[A]): Future[Throwable \/ A] =
      fa.transform {
        case SuccessS(a) =>
          Try(a.right[Throwable])

        case FailureS(NonFatal(ex)) =>
          Try(ex.left[A])

        case FailureS(ex) =>
          throw ex
      }(EC0)

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def catchNonFatal[A, B](fb: => Future[B])(f: Throwable => A): Future[A \/ B] =
      fb.transform {
        case SuccessS(b) =>
          Try(b.right[A])

        case FailureS(NonFatal(ex)) =>
          Try(f(ex).left[B])

        case FailureS(ex) =>
          throw ex
      }(EC0)

    override def catchNonFatalEither[A, B](fab: => Future[A \/ B])(f: Throwable => A): Future[A \/ B] =
      catchNonFatal(fab)(f).map(_.flatMap(identity))(EC0)
  }

  implicit val canCatchId: CanCatch[Id] = new CanCatch[Id] {

    override def catchNonFatalThrowable[A](fa: => Scalaz.Id[A]): Scalaz.Id[Throwable \/ A] =
      Try(fa) match {
        case SuccessS(a) =>
          a.right[Throwable]

        case FailureS(NonFatal(ex)) =>
          ex.left[A]

        case FailureS(ex) =>
          throw ex
      }

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def catchNonFatal[A, B](fb: => Id[B])(f: Throwable => A): Id[A \/ B] =
      Try(fb) match {
        case SuccessS(b) =>
          b.right[A]

        case FailureS(NonFatal(ex)) =>
          f(ex).left[B]

        case FailureS(ex) =>
          throw ex
      }

    override def catchNonFatalEither[A, B](fab: => Id[A \/ B])(f: Throwable => A): Id[A \/ B] =
      catchNonFatal(fab)(f).flatMap(identity)
  }

}
