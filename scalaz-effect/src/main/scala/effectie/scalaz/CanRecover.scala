package effectie.scalaz

import scalaz.{Scalaz, _}
import Scalaz._
import scalaz.effect._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
trait CanRecover[F[_]] extends effectie.CanRecover[F] {
  override type Xor[+A, +B]  = A \/ B
  override type XorT[A, B] = EitherT[F, A, B]

  @inline override protected def xorT[A, B](fab: => F[A \/ B]): DisjunctionT[F, A, B] =
    EitherT(fab)

  @inline override protected def xorT2FXor[A, B](efab: => DisjunctionT[F, A, B]): F[A \/ B] =
    efab.run
}

object CanRecover {
  def apply[F[_]: CanRecover]: CanRecover[F] = implicitly[CanRecover[F]]

  implicit object IoRecoverable extends CanRecover[IO] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => IO[A]
    )(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      fa.attempt.flatMap {
        case -\/(NonFatal(ex)) =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err)
        case -\/(ex)           =>
          IO(throw ex)
        case \/-(a)            =>
          IO[AA](a)
      }

    override def recoverFromNonFatal[A, AA >: A](
      fa: => IO[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(IO(_)))

  }

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  implicit def futureRecoverable(implicit ec: ExecutionContext): CanRecover[Future] =
    new effectie.CanRecover.FutureCanRecover(ec) with CanRecover[Future]

  implicit object IdRecoverable extends CanRecover[Id] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Id[A]
    )(
      handleError: PartialFunction[Throwable, Id[AA]]
    ): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex)  =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err)
        case ex: Throwable =>
          throw ex
      }

    override def recoverFromNonFatal[A, AA >: A](
      fa: => Scalaz.Id[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): Scalaz.Id[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError)

  }

}
