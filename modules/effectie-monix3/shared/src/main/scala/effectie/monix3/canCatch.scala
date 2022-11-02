package effectie.monix3

import effectie.core.CanCatch
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  implicit object canCatchTask extends CanCatch[Task] {

    @inline override final def mapFa[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)

    @inline override def catchNonFatalThrowable[A](fa: => Task[A]): Task[Either[Throwable, A]] =
      fa.attempt

  }

}
