package effectie.cats

import cats.data.EitherT
import cats.syntax.all.*
import cats.{Applicative, Functor}

trait EitherTSupport {

  import EitherTSupport.*

  def eitherTOf[F[_]]: PartiallyAppliedEitherTEffectOf[F] =
    new PartiallyAppliedEitherTEffectOf[F]

  def eitherTOfPure[F[_]]: PartiallyAppliedEitherTEffectOfPure[F] =
    new PartiallyAppliedEitherTEffectOfPure[F]

  def eitherTRight[A]: PartiallyAppliedEitherTRightEffectOf[A] =
    new PartiallyAppliedEitherTRightEffectOf[A]

  def eitherTRightPure[A]: PartiallyAppliedEitherTRightEffectOfPure[A] =
    new PartiallyAppliedEitherTRightEffectOfPure[A]

  def eitherTLeft[B]: PartiallyAppliedEitherTLeftEffectOf[B] =
    new PartiallyAppliedEitherTLeftEffectOf[B]

  def eitherTLeftPure[B]: PartiallyAppliedEitherTLeftEffectOfPure[B] =
    new PartiallyAppliedEitherTLeftEffectOfPure[B]

  def eitherTRightF[A]: PartiallyAppliedEitherTRightF[A] =
    new PartiallyAppliedEitherTRightF[A]

  def eitherTLeftF[B]: PartiallyAppliedEitherTLeftF[B] =
    new PartiallyAppliedEitherTLeftF[B]

}

object EitherTSupport extends EitherTSupport {

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: => Either[A, B])(using EF: FxCtor[F]): EitherT[F, A, B] =
      EitherT(FxCtor[F].effectOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: Either[A, B])(using EF: FxCtor[F]): EitherT[F, A, B] =
      EitherT(FxCtor[F].pureOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: => B)(using EC: FxCtor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(FxCtor[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: B)(using EC: FxCtor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(FxCtor[F].pureOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: => A)(using EC: FxCtor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(FxCtor[F].effectOf(a).map(_.asLeft[B]))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: A)(using EC: FxCtor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(FxCtor[F].pureOf(a).map(_.asLeft[B]))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightF[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_]: Functor, B](b: F[B]): EitherT[F, A, B] =
      EitherT.liftF(b)
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftF[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_]: Functor, A](a: F[A]): EitherT[F, A, B] =
      EitherT(a.map(_.asLeft[B]))
  }

  extension [F[_], A, B](fOfEither: F[Either[A, B]]) {
    def eitherT: EitherT[F, A, B] = EitherT[F, A, B](fOfEither)
  }

  extension [A, B](either: Either[A, B]) {
    def eitherT[F[_]: Applicative]: EitherT[F, A, B] = EitherT.fromEither[F](either)
  }

  extension [F[_], A](fa: F[A]) {
    def rightT[B](using F: Functor[F]): EitherT[F, B, A] = EitherT.right[B](fa)
    def leftT[B](using F: Functor[F]): EitherT[F, A, B]  = EitherT.left[B](fa)
  }

  extension [A](a: A) {
    def rightTF[F[_]: Applicative, B]: EitherT[F, B, A] = EitherT.rightT[F, B](a)
    def leftTF[F[_]: Applicative, B]: EitherT[F, A, B]  = EitherT.leftT[F, B](a)
  }

}
