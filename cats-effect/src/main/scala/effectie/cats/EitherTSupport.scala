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

  def eitherTRight[F[_], A]: PartiallyAppliedEitherTRightEffectOf[F, A] =
    new PartiallyAppliedEitherTRightEffectOf[F, A]

  def eitherTRightPure[F[_], A]: PartiallyAppliedEitherTRightEffectOfPure[F, A] =
    new PartiallyAppliedEitherTRightEffectOfPure[F, A]

  def eitherTLeft[F[_], B]: PartiallyAppliedEitherTLeftEffectOf[F, B] =
    new PartiallyAppliedEitherTLeftEffectOf[F, B]

  def eitherTLeftPure[F[_], B]: PartiallyAppliedEitherTLeftEffectOfPure[F, B] =
    new PartiallyAppliedEitherTLeftEffectOfPure[F, B]

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
      EitherT(EffectConstructor[F].effectOfPure(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[F[_], A] {
    def apply[B](b: => B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(EffectConstructor[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[F[_], A] {
    def apply[B](b: B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(EffectConstructor[F].effectOfPure(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[F[_], B] {
    def apply[A](a: => A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOf(a).map(_.asLeft[B]))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[F[_], B] {
    def apply[A](a: A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOfPure(a).map(_.asLeft[B]))
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
