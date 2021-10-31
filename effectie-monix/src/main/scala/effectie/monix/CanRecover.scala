package effectie.monix

import cats._
import cats.data.EitherT
import monix.eval.Task

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanRecover[F[_]] extends effectie.CanRecover[F] {
  type Xor[+A, +B]  = Either[A, B]
  type XorT[A, B] = EitherT[F, A, B]

  @inline override final protected def xorT[A, B](fab: F[Either[A, B]]): EitherT[F, A, B] =
    EitherT(fab)

  @inline override final protected def xorT2FXor[A, B](efab: EitherT[F, A, B]): F[Either[A, B]] =
    efab.value
}

object CanRecover {

  def apply[F[_]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

  implicit object IoCanRecover extends CanRecover[Task] {
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, Task[AA]]): Task[AA] =
      fa.onErrorRecoverWith(handleError)

    override def recoverFromNonFatal[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, AA]): Task[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(Task.pure(_)))

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureCanRecover(
    implicit ec: ExecutionContext
  ): CanRecover[Future] =
    new effectie.CanRecover.FutureCanRecover(ec) with CanRecover[Future]

  implicit object IdCanRecover extends CanRecover[Id] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Id[A]
    )(handleError: PartialFunction[Throwable, Id[AA]]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex)  =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err)
        case ex: Throwable =>
          throw ex
      }

    override def recoverFromNonFatal[A, AA >: A](
      fa: => Id[A]
    )(handleError: PartialFunction[Throwable, AA]): Id[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError)

  }

}
