package effectie.instances.id

import cats.Id
import cats.syntax.all._
import effectie.core.{CanCatch, FxCtor}

/** @author Kevin Lee
  * @since 2020-06-07
  */
object canCatch {

  implicit object canCatchId extends CanCatch[Id] {

    override implicit protected val fxCtor: FxCtor[Id] = effectie.instances.id.fxCtor.idFxCtor

    @inline override final def flatMapFa[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = f(fa)

    @inline override final def catchNonFatalThrowable[A](fa: => Id[A]): Id[Either[Throwable, A]] =
      scala.util.Try(fa) match {
        case scala.util.Success(a) =>
          a.asRight[Throwable]

        case scala.util.Failure(scala.util.control.NonFatal(ex)) =>
          ex.asLeft[A]

        case scala.util.Failure(ex) =>
          throw ex // scalafix:ok DisableSyntax.throw
      }
  }

}
