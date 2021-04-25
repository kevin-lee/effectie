package effectie.scalaz

import scalaz._
import Scalaz._

trait OptionTSupport {

  import OptionTSupport._

  def optionTOf[F[_]]: PartiallyAppliedOptionTOf[F] =
    new PartiallyAppliedOptionTOf[F]

  def optionTOfPure[F[_]]: PartiallyAppliedOptionTOfPure[F] =
    new PartiallyAppliedOptionTOfPure[F]

  def optionTSome[F[_]]: PartiallyAppliedOptionTSome[F] =
    new PartiallyAppliedOptionTSome[F]

  def optionTSomePure[F[_]]: PartiallyAppliedOptionTSomePure[F] =
    new PartiallyAppliedOptionTSomePure[F]

  def optionTNone[F[_]: EffectConstructor, A]: OptionT[F, A] =
    OptionT[F, A](EffectConstructor[F].pureOf(none[A]))

  def optionTSomeF[F[_]: Functor, A](fa: F[A]): OptionT[F, A] =
    OptionT[F, A](fa.map(_.some))

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object OptionTSupport extends OptionTSupport {

  private[OptionTSupport] final class PartiallyAppliedOptionTOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => Option[A])(implicit EF: EffectConstructor[F]): OptionT[F, A] =
      OptionT(EffectConstructor[F].effectOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: Option[A])(implicit EF: EffectConstructor[F]): OptionT[F, A] =
      OptionT(EffectConstructor[F].pureOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSome[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EC: EffectConstructor[F], FT: Functor[F]): OptionT[F, A] =
      OptionT(EC.effectOf(a).map(_.some))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSomePure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EC: EffectConstructor[F], FT: Functor[F]): OptionT[F, A] =
      OptionT(EC.pureOf(a).map(_.some))
  }

}
