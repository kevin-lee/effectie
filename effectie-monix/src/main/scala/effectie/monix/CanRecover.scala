package effectie.monix

import cats._
import cats.effect.IO
import monix.eval.Task

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
object CanRecover {

  type CanRecover[F[_]] = effectie.CanRecover[F]

  implicit object TaskCanRecover extends CanRecover[Task] {
    override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, Task[AA]]): Task[AA] =
      fa.onErrorRecoverWith(handleError)

    override def recoverFromNonFatal[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, AA]): Task[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(Task.pure(_)))

  }

  implicit object IoCanRecover extends CanRecover[IO] {

    override def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      fa.handleErrorWith(err => handleError.applyOrElse(err, ApplicativeError[IO, Throwable].raiseError[AA]))

    override def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(handleError: PartialFunction[Throwable, AA]): IO[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(IO.pure(_)))

  }

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
