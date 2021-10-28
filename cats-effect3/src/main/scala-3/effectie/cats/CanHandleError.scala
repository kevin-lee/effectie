package effectie.cats

import cats.Id
import cats.data.EitherT
import cats.effect.IO

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanHandleError[F[_]] extends effectie.CanHandleError[F] {

  type Xor[+A, +B]  = Either[A, B]
  type XorT[A, B] = EitherT[F, A, B]

  inline override protected def xorT[A, B](fab: F[Either[A, B]]): EitherT[F, A, B] =
    EitherT(fab)

  inline override protected def xorT2FXor[A, B](efab: EitherT[F, A, B]): F[Either[A, B]] =
    efab.value

}

object CanHandleError {

  def apply[F[_]: CanHandleError]: CanHandleError[F] = summon[CanHandleError[F]]

  given ioCanHandleError: CanHandleError[IO] with {

    override def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.handleErrorWith(handleError)

    override def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO.pure(handleError(err)))

  }

  given futureCanHandleError(using ec: ExecutionContext): CanHandleError[Future] =
    new effectie.CanHandleError.FutureCanHandleError(ec) with CanHandleError[Future]

  given idCanHandleError: CanHandleError[Id] with {

    override def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError(ex)
      }

    override def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      handleNonFatalWith[A, AA](fa)(err => handleError(err))

  }

}
