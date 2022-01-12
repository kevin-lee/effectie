package effectie.testing

import _root_.cats.{Eq, Show}

/** @author Kevin Lee
  * @since 2021-10-30
  */
object types {

  sealed trait SomeError
  object SomeError {

    final case class SomeThrowable(throwable: Throwable) extends SomeError
    final case class Message(message: String) extends SomeError

    def someThrowable(throwable: Throwable): SomeError = SomeThrowable(throwable)

    def message(message: String): SomeError = Message(message)

    implicit val someErrorEq: Eq[SomeError] = Eq.fromUniversalEquals

    implicit val someErrorShow: Show[SomeError] = _.toString
  }

  abstract class SomeThrowableError(val message: String, val cause: Throwable) extends RuntimeException(message, cause)
  object SomeThrowableError {
    final case class Message(override val message: String) extends SomeThrowableError(message, null)
    final case class SomeThrowable(override val cause: Throwable) extends SomeThrowableError(cause.getMessage, cause)

    def message(message: String): SomeThrowableError        = Message(message)
    def someThrowable(cause: Throwable): SomeThrowableError = SomeThrowable(cause)

    implicit val someThrowableErrorEq: Eq[SomeThrowableError] = Eq.fromUniversalEquals

    implicit val someThrowableErrorShow: Show[SomeThrowableError] = _.toString
  }

}
