package effectie.monix3

import effectie.core.CanRecover
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canRecover {

  implicit object taskCanRecover extends CanRecover[Task] {
    @inline override final def recoverFromNonFatalWith[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, Task[AA]]): Task[AA] =
      fa.onErrorRecoverWith(handleError)

    @inline override final def recoverFromNonFatal[A, AA >: A](
      fa: => Task[A]
    )(handleError: PartialFunction[Throwable, AA]): Task[AA] =
      recoverFromNonFatalWith[A, AA](fa)(handleError.andThen(Task.pure(_)))

  }

}
