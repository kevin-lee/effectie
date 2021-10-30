package effectie.testing

/**
 * @author Kevin Lee
 * @since 2021-10-30
 */
object types {

  sealed trait SomeError
  object SomeError {

    final case class SomeThrowable(throwable: Throwable) extends SomeError
    final case class Message(message: String)            extends SomeError

    def someThrowable(throwable: Throwable): SomeError = SomeThrowable(throwable)

    def message(message: String): SomeError = Message(message)

  }
}
