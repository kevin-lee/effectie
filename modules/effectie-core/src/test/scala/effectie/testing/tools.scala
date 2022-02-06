package effectie.testing

import hedgehog._

import java.io.{PrintWriter, StringWriter}
import scala.util.control.NonFatal

/** @author
  *   Kevin Lee
  * @since
  *   2021-05-16
  */
object tools {

  implicit final class ThrowableOps(private val throwable: Throwable) extends AnyVal {
    def stackTraceString: String = {
      val stringWriter = new StringWriter()
      val printWriter  = new PrintWriter(stringWriter)
      throwable.printStackTrace(printWriter)
      stringWriter.toString
    }
  }

  def dropResult[A](a: => A): Unit = {
    val _ = a
    ()
  }

  def expectThrowable[A](a: => A, expected: Throwable*): Result = {
    val moreThanOne              = expected.length > 1
    val expectedThrowableMessage =
      s"${if (moreThanOne) "One of " else ""}${expected.map(_.getClass.getName).mkString("[", ", ", "]")} " +
        s"${if (moreThanOne) "were" else "was"} expected"
    try {
      dropResult(a)
      Result.failure.log(s"$expectedThrowableMessage but no Throwable was not thrown.")
    } catch {
      case NonFatal(ex) =>
        Result
          .assert(expected.contains(ex))
          .log(
            expectedThrowableMessage +
              s" but ${ex.getClass.getName} was thrown instead.\n${ex.stackTraceString}"
          )
    }
  }
}
