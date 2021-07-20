package effectie.cats

import cats.{Applicative, Functor}
import cats.data.EitherT
import cats.syntax.all._

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
object EitherTSupport extends EitherTSupport {

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: => Either[A, B])(implicit EF: Eft[F]): EitherT[F, A, B] =
      EitherT(Eft[F].effectOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTEffectOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A, B](ab: Either[A, B])(implicit EF: Eft[F]): EitherT[F, A, B] =
      EitherT(Eft[F].pureOf(ab))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOf[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: => B)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(Eft[F].effectOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTRightEffectOfPure[A](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], B](b: B)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT.liftF(Eft[F].pureOf(b))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOf[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: => A)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(Eft[F].effectOf(a).map(_.asLeft[B]))
  }

  private[EitherTSupport] final class PartiallyAppliedEitherTLeftEffectOfPure[B](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[F[_], A](a: A)(implicit EC: Eft[F], FT: Functor[F]): EitherT[F, A, B] =
      EitherT(Eft[F].pureOf(a).map(_.asLeft[B]))
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

  implicit final class EitherTFEitherOps[F[_], A, B](private val fOfEither: F[Either[A, B]]) extends AnyVal {
    def eitherT: EitherT[F, A, B] = EitherT[F, A, B](fOfEither)
  }

  implicit final class EitherTEitherOps[A, B](private val either: Either[A, B]) extends AnyVal {
    def eitherT[F[_]: Applicative]: EitherT[F, A, B] = EitherT.fromEither[F](either)
  }

  implicit final class EitherTFAOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def rightT[B](implicit F: Functor[F]): EitherT[F, B, A] = EitherT.right[B](fa)
    def leftT[B](implicit F: Functor[F]): EitherT[F, A, B]  = EitherT.left[B](fa)
  }

  implicit final class EitherTAOps[A](private val a: A) extends AnyVal {
    def rightTF[F[_]: Applicative, B]: EitherT[F, B, A] = EitherT.rightT[F, B](a)
    def leftTF[F[_]: Applicative, B]: EitherT[F, A, B]  = EitherT.leftT[F, B](a)
  }

}
