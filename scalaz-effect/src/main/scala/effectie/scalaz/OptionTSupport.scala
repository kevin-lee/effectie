package effectie.scalaz

import scalaz._
import Scalaz._

trait OptionTSupport {

  import OptionTSupport._

  def optionTOf[A]: PartiallyAppliedOptionTOf[A] =
    new PartiallyAppliedOptionTOf[A]

  def optionTOfPure[A]: PartiallyAppliedOptionTOfPure[A] =
    new PartiallyAppliedOptionTOfPure[A]

  def optionTSome[F[_]]: PartiallyAppliedOptionTSome[F] =
    new PartiallyAppliedOptionTSome[F]

  def optionTSomePure[F[_]]: PartiallyAppliedOptionTSomePure[F] =
    new PartiallyAppliedOptionTSomePure[F]

  def optionTNone[F[_]: EffectConstructor, A]: OptionT[F, A] =
    OptionT[F, A](EffectConstructor[F].effectOfPure(none[A]))

  def optionTSomeF[F[_]: Functor, A](fa: F[A]): OptionT[F, A] =
    OptionT[F, A](fa.map(_.some))

}

object OptionTSupport extends OptionTSupport {

  private[OptionTSupport] final class PartiallyAppliedOptionTOf[A] {
    def apply[F[_]: EffectConstructor](a: => Option[A]): OptionT[F, A] =
      OptionT(EffectConstructor[F].effectOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTOfPure[A] {
    def apply[F[_]: EffectConstructor](a: Option[A]): OptionT[F, A] =
      OptionT(EffectConstructor[F].effectOfPure(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSome[F[_]] {
    def apply[A](a: => A)(implicit EC: EffectConstructor[F], FT: Functor[F]): OptionT[F, A] =
      OptionT(EC.effectOf(a).map(_.some))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSomePure[F[_]] {
    def apply[A](a: A)(implicit EC: EffectConstructor[F], FT: Functor[F]): OptionT[F, A] =
      OptionT(EC.effectOfPure(a).map(_.some))
  }

}
