package effectie.instances.id

import cats.Id
import effectie.core.CanHandleError

import scala.util.control.NonFatal

/** @author Kevin Lee
  * @since 2020-08-17
  */
object canHandleError {

  implicit object idCanHandleError extends CanHandleError[Id] {

    @inline override final def handleNonFatalWith[A, AA >: A](fa: => Id[A])(handleError: Throwable => Id[AA]): Id[AA] =
      try (fa)
      catch {
        case NonFatal(ex) =>
          handleError(ex)
      }

    @inline override final def handleNonFatal[A, AA >: A](fa: => Id[A])(handleError: Throwable => AA): Id[AA] =
      handleNonFatalWith[A, AA](fa)(err => handleError(err))

  }

}
