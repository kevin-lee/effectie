package effectie.cats

import cats.Functor
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

}
