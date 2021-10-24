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
  type Xor[A, B]  = Either[A, B]
  type XorT[A, B] = EitherT[F, A, B]
}

object CanHandleError {
  def apply[F[_]: CanHandleError]: CanHandleError[F] = summon[CanHandleError[F]]

  given ioCanHandleError: CanHandleError[IO] with {


    override def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.handleErrorWith(handleError)

    override def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
      fab: => IO[Either[A, B]]
    )(
      handleError: Throwable => IO[Either[AA, BB]]
    ): IO[Either[AA, BB]] =
      handleNonFatalWith(fab)(handleError)

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: Throwable => IO[Either[AA, BB]]
    ): EitherT[IO, AA, BB] =
      EitherT(handleNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError))

    override def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO.pure(handleError(err)))

    override def handleEitherNonFatal[A, AA >: A, B, BB >: B](
      fab: => IO[Either[A, B]]
    )(
      handleError: Throwable => Either[AA, BB]
    ): IO[Either[AA, BB]] =
      handleNonFatal(fab)(handleError)

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[IO, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => IO.pure(handleError(err)))

  }

  final class FutureCanHandleError(override val ec: ExecutionContext)
      extends effectie.CanHandleError.FutureCanHandleError(ec)
      with CanHandleError[Future] {


    override def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
      fab: => Future[Either[A, B]]
    )(
      handleError: Throwable => Future[Either[AA, BB]]
    ): Future[Either[AA, BB]] =
      handleNonFatalWith(fab)(handleError)

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: Throwable => Future[Either[AA, BB]]
    ): EitherT[Future, AA, BB] =
      EitherT(
        efab
          .value
          .recoverWith {
            case throwable: Throwable =>
              handleError(throwable)
          }(ec)
      )

    override def handleEitherNonFatal[A, AA >: A, B, BB >: B](
      fab: => Future[Either[A, B]]
    )(
      handleError: Throwable => Either[AA, BB]
    ): Future[Either[AA, BB]] =
      handleNonFatal(fab)(handleError)

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[Future, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => Future(handleError(err))(ec))

  }

  given futureCanHandleError(using ec: ExecutionContext): CanHandleError[Future] =
    new FutureCanHandleError(ec)

  given idCanHandleError: CanHandleError[Id] with {

    override def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError(ex)
      }

    override def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
      fab: => Id[Either[A, B]]
    )(
      handleError: Throwable => Id[Either[AA, BB]]
    ): Id[Either[AA, BB]] =
      handleNonFatalWith(fab)(handleError)

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: Throwable => Id[Either[AA, BB]]
    ): EitherT[Id, AA, BB] =
      EitherT(
        try (efab.value)
        catch {
          case NonFatal(ex) =>
            handleError(ex)
        }
      )

    override def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      handleNonFatalWith[A, AA](fa)(err => handleError(err))

    override def handleEitherNonFatal[A, AA >: A, B, BB >: B](
      fab: => Id[Either[A, B]]
    )(
      handleError: Throwable => Either[AA, BB]
    ): Id[Either[AA, BB]] =
      handleNonFatal(fab)(handleError)

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[Id, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => handleError(err))
  }

}
