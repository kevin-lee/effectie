package effectie.cats

import cats.Functor
import cats.data.EitherT
import cats.implicits._

trait EitherTSupport {

  import EitherTSupport._

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

object EitherTSupport extends  EitherTSupport {

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOf[F[_]] {
    def apply[A, B](ab: => Either[A, B])(implicit EF: EffectConstructor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOfPure[F[_]] {
    def apply[A, B](ab: Either[A, B])(implicit EF: EffectConstructor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].pureOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[A] {
    def apply[F[_], B](b: => B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(EffectConstructor[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[A] {
    def apply[F[_], B](b: B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(EffectConstructor[F].pureOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[B] {
    def apply[F[_], A](a: => A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOf(a).map(_.asLeft[B]))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[B] {
    def apply[F[_], A](a: A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].pureOf(a).map(_.asLeft[B]))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightF[A] {
    def apply[F[_]: Functor, B](b: F[B]): EitherT[F, A, B] =
      EitherT.liftF(b)
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftF[B] {
    def apply[F[_]: Functor, A](a: F[A]): EitherT[F, A, B] =
      EitherT(a.map(_.asLeft[B]))
  }

}
