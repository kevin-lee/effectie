package effectie.compat

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

/**
 * @author Kevin Lee
 * @since 2020-06-10
 */
object FutureCompat {

  @SuppressWarnings(Array("org.wartremover.warts.ImplicitParameter"))
  def transform[A, B](
    future: Future[A]
  )(
    f: Try[A] => Try[B]
  )(implicit executor: ExecutionContext): Future[B] = {
    val p = Promise[B]()
    future.onComplete( complete =>
      p.complete(f(complete))
    )
    p.future
  }

}
