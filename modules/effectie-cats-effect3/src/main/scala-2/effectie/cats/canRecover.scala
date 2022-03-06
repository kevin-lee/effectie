package effectie.cats

import cats._
import cats.effect._
import effectie.core.CanRecover

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canRecover {

  implicit object ioCanRecover extends CanRecover[IO] {

    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, IO[AA]]
    ): IO[AA] =
      fa.handleErrorWith(err => handleError.applyOrElse(err, ApplicativeError[IO, Throwable].raiseError[AA]))

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => IO[A])(
      handleError: PartialFunction[Throwable, AA]
    ): IO[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(IO.pure(_)))
  }

  implicit object idCanRecover extends CanRecover[Id] {

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    @inline override final def recoverFromNonFatalWith[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, Id[AA]]
    ): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError.applyOrElse(ex, (err: Throwable) => throw err) // scalafix:ok DisableSyntax.throw
        case ex: Throwable =>
          throw ex // scalafix:ok DisableSyntax.throw
      }

    @inline override final def recoverFromNonFatal[A, AA >: A](fa: => Id[A])(
      handleError: PartialFunction[Throwable, AA]
    ): Id[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError)

  }

}
