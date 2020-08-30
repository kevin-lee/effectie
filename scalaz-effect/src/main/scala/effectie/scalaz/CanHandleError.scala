package effectie.scalaz

import scalaz._
import Scalaz._
import scalaz.effect._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


/**
 * @author Kevin Lee
 * @since 2020-08-17
 */
trait CanHandleError[F[_]] extends effectie.CanHandleError[F] {
  type Xor[A, B] = A \/ B
  type XorT[A, B] = EitherT[F, A, B]
}

object CanHandleError {
  def apply[F[_]: CanHandleError]: CanHandleError[F] = implicitly[CanHandleError[F]]

  implicit val ioCanHandleError: CanHandleError[IO] = new CanHandleError[IO] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def handleNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: Throwable => IO[AA]): IO[AA] =
      fa.attempt.flatMap {
        case -\/(NonFatal(ex)) =>
          handleError(ex)
        case -\/(ex) =>
          throw ex
        case \/-(a) =>
          IO[AA](a)
      }

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: Throwable => IO[AA \/ BB]
    ): EitherT[IO, AA, BB] =
      EitherT(handleNonFatalWith[A \/ B, AA \/ BB](efab.run)(handleError))

    override def handleNonFatal[A, AA >: A](fa: => IO[A])(handleError: Throwable => AA): IO[AA] =
      handleNonFatalWith[A, AA](fa)(err => IO(handleError(err)))

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: Throwable => AA \/ BB
    ): EitherT[IO, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => IO(handleError(err)))

  }

  final class FutureCanHandleError(override val ec: ExecutionContext)
    extends effectie.CanHandleError.FutureCanHandleError(ec)
      with CanHandleError[Future] {

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: Throwable => Future[AA \/ BB]
    ): EitherT[Future, AA, BB] =
      EitherT(
        efab.run.recoverWith {
          case throwable: Throwable =>
            handleError(throwable)
        }(ec)
      )

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: Throwable => AA \/ BB
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

    override def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: Throwable => Id[AA \/ BB]
    ): EitherT[Id, AA, BB] =
      EitherT(
        try (efab.run)
        catch {
          case NonFatal(ex) =>
            handleError(ex)
        }
      )

    override def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      handleNonFatalWith[A, AA](fa)(err => handleError(err))

    override def handleEitherTNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: Throwable => AA \/ BB
    ): EitherT[Id, AA, BB] =
      handleEitherTNonFatalWith[A, AA, B, BB](efab)(err => handleError(err))
  }

}