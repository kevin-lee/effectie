package effectie.scalaz

import scalaz.{Applicative, EitherT, Functor, \/}

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
    def apply[A, B](ab: => A \/ B)(implicit EF: Eft[F]): EitherT[F, A, B] =
      EitherT(Eft[F].effectOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: A \/ B)(implicit EF: Eft[F]): EitherT[F, A, B] =
      EitherT(Eft[F].pureOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: => B)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.rightT(Eft[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: B)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.rightT(Eft[F].pureOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: => A)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.leftT(Eft[F].effectOf(a))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: A)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.leftT(Eft[F].pureOf(a))
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

  implicit final class EitherTFEitherOps[F[_], A, B](private val fOfEither: F[A \/ B]) extends AnyVal {
    def eitherT: EitherT[F, A, B] = EitherT[F, A, B](fOfEither)
  }

  implicit final class EitherTEitherOps[A, B](private val either: A \/ B) extends AnyVal {
    def eitherT[F[_]: Applicative]: EitherT[F, A, B] = EitherT.fromDisjunction[F](either)
  }

  implicit final class EitherTFAOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def rightT[B](implicit F: Functor[F]): EitherT[F, B, A] = EitherT.rightT[F, B, A](fa)
    def leftT[B](implicit F: Functor[F]): EitherT[F, A, B]  = EitherT.leftT[F, A, B](fa)
  }

  implicit final class EitherTAOps[A](private val a: A) extends AnyVal {
    def rightTF[F[_]: Applicative, B]: EitherT[F, B, A] = EitherT.pure[F, B, A](a)
    def leftTF[F[_]: Applicative, B]: EitherT[F, A, B]  = EitherT.pureLeft[F, A, B](a)
  }

}
