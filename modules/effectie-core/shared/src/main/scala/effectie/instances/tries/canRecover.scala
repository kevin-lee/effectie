package effectie.instances.tries

import effectie.core.CanRecover

import scala.util.Try

/** @author Kevin Lee
  * @since 2023-03-17
  */
object canRecover {

  trait TryCanRecover extends CanRecover[Try] {

    @inline override def recoverFromNonFatalWith[A, AA >: A](
      fa: => Try[A]
    )(
      handleError: PartialFunction[Throwable, Try[AA]]
    ): Try[AA] =
      fa.recoverWith(handleError)

    @SuppressWarnings(Array("org.wartremover.warts.Throw"))
    @inline override def recoverFromNonFatal[A, AA >: A](
      fa: => Try[A]
    )(
      handleError: PartialFunction[Throwable, AA]
    ): Try[AA] =
      fa.recover(handleError)
  }

  implicit object canRecoverTry extends TryCanRecover

}
