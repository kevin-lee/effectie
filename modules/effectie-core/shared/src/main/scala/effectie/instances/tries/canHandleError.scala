package effectie.instances.tries

import effectie.core.CanHandleError

import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object canHandleError {

  trait TryCanHandleError extends CanHandleError[Try] {

    @inline override def handleNonFatalWith[A, AA >: A](
      fa: => Try[A]
    )(handleError: Throwable => Try[AA]): Try[AA] =
      fa.recoverWith {
        case throwable: Throwable =>
          handleError(throwable)
      }

    @inline override def handleNonFatal[A, AA >: A](fa: => Try[A])(handleError: Throwable => AA): Try[AA] =
      fa.recover {
        case throwable: Throwable =>
          handleError(throwable)
      }
  }

  implicit object canHandleErrorTry extends TryCanHandleError
}
