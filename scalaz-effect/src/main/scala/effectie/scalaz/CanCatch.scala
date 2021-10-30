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
  override type Xor[+A, +B] = A \/ B
  override type XorT[A, B]  = EitherT[F, A, B]

  @inline override final protected def xorT[A, B](fab: F[A \/ B]): EitherT[F, A, B] = EitherT(fab)

  @inline override final protected def xorT2FXor[A, B](efab: EitherT[F, A, B]): F[A \/ B] = efab.run

  @inline override final protected def leftMapXor[A, AA, B](aOrB: A \/ B)(f: A => AA): AA \/ B =
    aOrB.leftMap(f)

  @inline override final protected def xorJoinRight[A, AA >: A, B](
    aOrB: AA \/ (A \/ B)
  ): AA \/ B =
    aOrB match {
      case \/-(b) =>
        b
      case -\/(_) =>
        aOrB.asInstanceOf[AA \/ B]
    }

}

object CanCatch {
  def apply[F[_]: CanCatch]: CanCatch[F] = implicitly[CanCatch[F]]

  implicit object CanCatchIo extends CanCatch[IO] {

    @inline override final protected def mapFa[A, B](fa: IO[A])(f: A => B): IO[B] = fa.map(f)

    override def catchNonFatalThrowable[A](fa: => IO[A]): IO[Throwable \/ A] =
      fa.attempt
        .map(_.leftMap {
          case NonFatal(ex) =>
            ex

          case ex =>
            throw ex
        })

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] =
    new effectie.CanCatch.CanCatchFuture(EC) with CanCatch[Future] {

      override def catchNonFatalThrowable[A](fa: => Future[A]): Future[Throwable \/ A] =
        fa.transform {
          case SuccessS(a) =>
            Try(a.right[Throwable])

          case FailureS(NonFatal(ex)) =>
            Try(ex.left[A])

          case FailureS(ex) =>
            throw ex
        }(EC0)

    }

  implicit object CanCatchId extends CanCatch[Id] {

    @inline override final protected def mapFa[A, B](fa: Id[A])(f: A => B): Id[B] =
      f(fa)

    override def catchNonFatalThrowable[A](fa: => Id[A]): Scalaz.Id[Throwable \/ A] =
      Try(fa) match {
        case SuccessS(a) =>
          a.right[Throwable]

        case FailureS(NonFatal(ex)) =>
          ex.left[A]

        case FailureS(ex) =>
          throw ex
      }

  }

}
