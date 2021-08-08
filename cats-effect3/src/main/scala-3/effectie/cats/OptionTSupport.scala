package effectie.cats

import cats.data.OptionT
import cats.{Applicative, Functor}

trait OptionTSupport {

  import OptionTSupport.*

  def optionTOf[F[_]]: PartiallyAppliedOptionTOf[F] =
    new PartiallyAppliedOptionTOf[F]

  def optionTOfPure[F[_]]: PartiallyAppliedOptionTOfPure[F] =
    new PartiallyAppliedOptionTOfPure[F]

  def optionTSome[F[_]]: PartiallyAppliedOptionTSome[F] =
    new PartiallyAppliedOptionTSome[F]

  def optionTSomePure[F[_]]: PartiallyAppliedOptionTSomePure[F] =
    new PartiallyAppliedOptionTSomePure[F]

  def optionTNone[F[_]: Applicative, A]: OptionT[F, A] =
    noneT[F, A]

  def noneT[F[_]: Applicative, A]: OptionT[F, A] =
    OptionT.none[F, A]

  def optionTSomeF[F[_]: Functor, A](fa: F[A]): OptionT[F, A] =
    OptionT.liftF[F, A](fa)

}

object OptionTSupport extends OptionTSupport {

  private[OptionTSupport] final class PartiallyAppliedOptionTOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => Option[A])(using EF: FxCtor[F]): OptionT[F, A] =
      OptionT(FxCtor[F].effectOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: Option[A])(using EF: FxCtor[F]): OptionT[F, A] =
      OptionT(FxCtor[F].pureOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSome[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(using EC: FxCtor[F], FT: Functor[F]): OptionT[F, A] =
      OptionT.liftF(EC.effectOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSomePure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(using EC: FxCtor[F], FT: Functor[F]): OptionT[F, A] =
      OptionT.liftF(EC.pureOf(a))
  }

  extension [F[_], A](fOfOption: F[Option[A]]) {
    def optionT: OptionT[F, A] = OptionT[F, A](fOfOption)
  }

  extension [A](option: Option[A]) {
    def optionT[F[_]: Applicative]: OptionT[F, A] = OptionT.fromOption[F](option)
  }

  extension [F[_], A](fa: F[A]) {
    def someT(using F: Functor[F]): OptionT[F, A] = OptionT.liftF(fa)
  }

  extension [A](a: A) {
    def someTF[F[_]: Applicative]: OptionT[F, A] = OptionT.some[F](a)
  }

}
