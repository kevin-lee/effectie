package effectie.monix

import cats.Id
import cats.data.EitherT
import monix.eval.Task

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
  def apply[F[_]: CanHandleError]: CanHandleError[F] = implicitly[CanHandleError[F]]

  implicit val ioCanHandleError: CanHandleError[Task] = new CanHandleError[Task] {

    override def handleNonFatalWith[A, AA >: A](fa: => Task[A])(handleError: Throwable => Task[AA]): Task[AA] =
      fa.onErrorHandleWith(handleError)

    override def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
      fab: => Task[Either[A, B]]
    )(
      handleError: Throwable => Task[Either[AA, BB]]
    ): Task[Either[AA, BB]] =
      handleNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Task, A, B]
    )(
      handleError: Throwable => Task[Either[AA, BB]]
    ): EitherT[Task, AA, BB] =
      EitherT(handleNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError))

    override def handleNonFatal[A, AA >: A](fa: => Task[A])(handleError: Throwable => AA): Task[AA] =
      handleNonFatalWith[A, AA](fa)(err => Task.pure(handleError(err)))

    override def handleEitherNonFatal[A, AA >: A, B, BB >: B](
      fab: => Task[Either[A, B]]
    )(
      handleError: Throwable => Either[AA, BB]
    ): Task[Either[AA, BB]] =
      handleNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Task, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[Task, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => Task.pure(handleError(err)))

  }

  final class FutureCanHandleError(override val ec: ExecutionContext)
      extends effectie.CanHandleError.FutureCanHandleError(ec)
      with CanHandleError[Future] {


    override def handleEitherNonFatalWith[A, AA >: A, B, BB >: B](
      fab: => Future[Either[A, B]]
    )(
      handleError: Throwable => Future[Either[AA, BB]]
    ): Future[Either[AA, BB]] =
      handleNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

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
      handleNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[Future, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => Future(handleError(err))(ec))

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureCanHandleError(implicit ec: ExecutionContext): CanHandleError[Future] =
    new FutureCanHandleError(ec)

  implicit val idCanHandleError: CanHandleError[Id] = new CanHandleError[Id] {

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
      handleNonFatalWith[Either[A, B], Either[AA, BB]](fab)(handleError)

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
      handleNonFatal[Either[A, B], Either[AA, BB]](fab)(handleError)

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: Throwable => Either[AA, BB]
    ): EitherT[Id, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => handleError(err))
  }

}
