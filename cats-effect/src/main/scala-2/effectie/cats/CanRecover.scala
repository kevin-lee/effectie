package effectie.cats

import cats._
import cats.data.EitherT
import cats.effect._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


/**
 * @author Kevin Lee
 * @since 2020-08-17
 */
trait CanRecover[F[_]] extends effectie.CanRecover[F] {
  type Xor[A, B] = Either[A, B]
  type XorT[A, B] = EitherT[F, A, B]
}

object CanRecover {

  def apply[F[_]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

  implicit val ioCanRecover: CanRecover[IO] = new CanRecover[IO] {
    override def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(handleError: PartialFunction[Throwable, IO[AA]]): IO[AA] =
      fa.handleErrorWith(err => handleError.applyOrElse(err, ApplicativeError[IO, Throwable].raiseError[AA]))

    override def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: PartialFunction[Throwable, IO[Either[AA, BB]]]
    ): EitherT[IO, AA, BB] =
      EitherT(recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError))

    override def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(handleError: PartialFunction[Throwable, AA]): IO[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(IO.pure(_)))

    override def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    ): EitherT[IO, AA, BB] =
      recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(handleError.andThen(IO.pure(_)))

  }

  final class FutureCanRecover(override val ec: ExecutionContext)
    extends effectie.CanRecover.FutureCanRecover(ec)
       with CanRecover[Future] {

    override def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: PartialFunction[Throwable, Future[Either[AA, BB]]]
    ): EitherT[Future, AA, BB] =
      EitherT(
        recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError)
      )

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    ): EitherT[Future, AA, BB] =
      recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(
        handleError.andThen(Future(_)(ec))
      )

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureCanRecover(implicit ec: ExecutionContext): CanRecover[Future] =
    new FutureCanRecover(ec)

  implicit val idCanRecover: CanRecover[Id] = new CanRecover[Id] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: PartialFunction[Throwable, Id[AA]]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err)
        case ex: Throwable =>
          throw ex
      }

    override def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: PartialFunction[Throwable, Id[Either[AA, BB]]]
    ): EitherT[Id, AA, BB] =
      EitherT(
        recoverFromNonFatalWith[Either[A, B], Either[AA, BB]](efab.value)(handleError)
      )

    override def recoverFromNonFatal[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Id[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError)

    override def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Id, A, B]
    )(
      handleError: PartialFunction[Throwable, Either[AA, BB]]
    ): EitherT[Id, AA, BB] =
      recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(handleError)

  }

}