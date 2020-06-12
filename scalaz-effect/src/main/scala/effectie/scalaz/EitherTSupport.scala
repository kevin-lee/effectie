package effectie.scalaz

import scalaz.{EitherT, Functor, \/}

trait EitherTSupport {

  import EitherTSupport._

  def eitherTOf[A, B]: PartiallyAppliedEitherTEffectOf[A, B] =
    new PartiallyAppliedEitherTEffectOf[A, B]

  def eitherTOfPure[A, B]: PartiallyAppliedEitherTEffectOfPure[A, B] =
    new PartiallyAppliedEitherTEffectOfPure[A, B]

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

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOf[A, B] {
    def apply[F[_]: EffectConstructor](ab: => A \/ B): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOfPure[A, B] {
    def apply[F[_]: EffectConstructor](ab: A \/ B): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOfPure(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[F[_], A] {
    def apply[B](b: => B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.rightT(EffectConstructor[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[F[_], A] {
    def apply[B](b: B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.rightT(EffectConstructor[F].effectOfPure(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[F[_], B] {
    def apply[A](a: => A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.leftT(EffectConstructor[F].effectOf(a))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[F[_], B] {
    def apply[A](a: A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.leftT(EffectConstructor[F].effectOfPure(a))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightF[A] {
    def apply[F[_]: Functor, B](b: F[B]): EitherT[F, A, B] =
      EitherT.rightT(b)
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftF[B] {
    def apply[F[_]: Functor, A](a: F[A]): EitherT[F, A, B] =
      EitherT.leftT(a)
  }

}
