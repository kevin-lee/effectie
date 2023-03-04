package effectie.instances.monix3

import effectie.core.{CanCatch, FxCtor}
import monix.eval.Task

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  implicit object canCatchTask extends CanCatch[Task] {

    override implicit protected val fxCtor: FxCtor[Task] = effectie.instances.monix3.fxCtor.taskFxCtor

    @inline override def catchNonFatalThrowable[A](fa: => Task[A]): Task[Either[Throwable, A]] =
      fa.attempt

  }

}
