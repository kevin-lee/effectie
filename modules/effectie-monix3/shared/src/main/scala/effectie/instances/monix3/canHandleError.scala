package effectie.instances.monix3

import effectie.core.CanHandleError
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleError {

  implicit object taskCanHandleError extends CanHandleError[Task] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Task[A])(
      handleError: Throwable => Task[AA]
    ): Task[AA] =
      fa.onErrorHandleWith(handleError)

    @inline override final def handleNonFatal[A, AA >: A](fa: => Task[A])(handleError: Throwable => AA): Task[AA] =
      handleNonFatalWith[A, AA](fa)(err => Task.pure(handleError(err)))

  }

}
