package effectie.instances.id

import cats.Id
import effectie.core.FxCtor

import scala.util.Try
import scala.util.control.NonFatal

object fxCtor {

  implicit object idFxCtor extends FxCtor[Id] {

    @inline override final def effectOf[A](a: => A): Id[A] = a

    @inline override final def pureOf[A](a: A): Id[A] = a

    @inline override final def pureOrError[A](a: => A): Id[A] =
      try pureOf(a)
      catch {
        case NonFatal(ex) => throw ex // scalafix:ok DisableSyntax.throw
      }

    @inline override val unitOf: Id[Unit] = ()

    @inline override final def errorOf[A](throwable: Throwable): Id[A] =
      throw throwable // scalafix:ok DisableSyntax.throw

    @inline override final def fromEither[A](either: Either[Throwable, A]): Id[A] = either.fold(errorOf, pureOf)

    @inline override final def fromOption[A](option: Option[A])(orElse: => Throwable): Id[A] =
      option.fold(errorOf(orElse))(pureOf)

    @inline override final def fromTry[A](tryA: Try[A]): Id[A] = tryA.fold(errorOf, pureOf)

  }

}
