package effectie.scalaz

import scalaz.{Scalaz, _}
import Scalaz._
import scalaz.effect._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal


/**
 * @author Kevin Lee
 * @since 2020-08-17
 */
trait CanRecover[F[_]] extends effectie.CanRecover[F] {
  override type Xor[A, B] = A \/ B
  override type XorT[A, B] = EitherT[F, A, B]
}

object CanRecover {
  def apply[F[_]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

  implicit val ioRecoverable: CanRecover[IO] = new CanRecover[IO] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => IO[A]
    )(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      fa.attempt.flatMap {
        case -\/(NonFatal(ex)) =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err)
        case -\/(ex) =>
          IO(throw ex)
        case \/-(a) =>
          IO[AA](a)
      }

    override def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: PartialFunction[Throwable, IO[AA \/ BB]]
    ): EitherT[IO, AA, BB] =
      EitherT(
        recoverFromNonFatalWith[A \/ B, AA \/ BB](efab.run)(handleError)
      )

    override def recoverFromNonFatal[A, AA >: A](
      fa: => IO[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(IO(_)))

    override def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[IO, A, B]
    )(
      handleError: PartialFunction[Throwable, AA \/ BB]
    ): EitherT[IO, AA, BB] =
      recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(handleError.andThen(IO(_)))

  }

  final class FutureCanRecover(override val ec: ExecutionContext)
    extends effectie.CanRecover.FutureCanRecover(ec)
       with CanRecover[Future] {

    override def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: PartialFunction[Throwable, Future[AA \/ BB]]
    ): EitherT[Future, AA, BB] =
      EitherT(
        recoverFromNonFatalWith[A \/ B, AA \/ BB](efab.run)(handleError)
      )

    override def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Future, A, B]
    )(
      handleError: PartialFunction[Throwable, AA \/ BB]
    ): EitherT[Future, AA, BB] =
      recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(
        handleError.andThen(Future(_)(ec))
      )

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureRecoverable(implicit ec: ExecutionContext): CanRecover[Future] =
    new FutureCanRecover(ec)

  implicit val idRecoverable: CanRecover[Id] = new CanRecover[Id] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Id[A]
    )(
      handleError: PartialFunction[Throwable, Id[AA]]
    ): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err)
        case ex: Throwable =>
          throw ex
      }

    override def recoverEitherTFromNonFatalWith[A, AA >: A, B, BB >: B](
      efab: => EitherT[Scalaz.Id, A, B]
    )(
      handleError: PartialFunction[Throwable, Scalaz.Id[AA \/ BB]]
    ): EitherT[Scalaz.Id, AA, BB] =
      EitherT(
        recoverFromNonFatalWith[A \/ B, AA \/ BB](efab.run)(handleError)
      )

    override def recoverFromNonFatal[A, AA >: A](
      fa: => Scalaz.Id[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): Scalaz.Id[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError)

    override def recoverEitherTFromNonFatal[A, AA >: A, B, BB >: B](
      efab: => EitherT[Scalaz.Id, A, B]
    )(
      handleError: PartialFunction[Throwable, AA \/ BB]
    ): EitherT[Scalaz.Id, AA, BB] =
      recoverEitherTFromNonFatalWith[A, AA, B, BB](efab)(handleError)

  }

}