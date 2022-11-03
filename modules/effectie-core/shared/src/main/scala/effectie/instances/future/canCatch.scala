package effectie.instances.future

import effectie.core.CanCatch

import scala.concurrent.{ExecutionContext, Future}

/** @author Kevin Lee
  * @since 2021-11-03
  */
object canCatch {

  trait FutureCanCatch extends CanCatch[Future] {
    implicit def EC0: ExecutionContext

    @inline override final def mapFa[A, B](fa: Future[A])(f: A => B): Future[B] =
      fa.map(f)(EC0)

    @inline override final def catchNonFatalThrowable[A](fa: => Future[A]): Future[Either[Throwable, A]] =
      fa.transform {
        case scala.util.Success(a) =>
          scala.util.Try[Either[Throwable, A]](Right(a))

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          scala.util.Try[Either[Throwable, A]](Left(ex))

        case scala.util.Failure(ex) =>
          throw ex // scalafix:ok DisableSyntax.throw
      }

  }

  final class CanCatchFuture(override implicit val EC0: ExecutionContext) extends FutureCanCatch

  implicit def canCatchFuture(implicit EC: ExecutionContext): CanCatch[Future] = new CanCatchFuture
}
