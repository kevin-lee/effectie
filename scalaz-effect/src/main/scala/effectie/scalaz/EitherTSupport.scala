package effectie.scalaz

import scalaz.{EitherT, Functor, \/}

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

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object EitherTSupport extends  EitherTSupport {

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: => A \/ B)(implicit EF: EffectConstructor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].effectOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: A \/ B)(implicit EF: EffectConstructor[F]): EitherT[F, A, B] =
      EitherT(EffectConstructor[F].pureOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: => B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.rightT(EffectConstructor[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: B)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.rightT(EffectConstructor[F].pureOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: => A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.leftT(EffectConstructor[F].effectOf(a))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: A)(implicit EC: EffectConstructor[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.leftT(EffectConstructor[F].pureOf(a))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightF[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_]: Functor, B](b: F[B]): EitherT[F, A, B] =
      EitherT.rightT(b)
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftF[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_]: Functor, A](a: F[A]): EitherT[F, A, B] =
      EitherT.leftT(a)
  }

}
