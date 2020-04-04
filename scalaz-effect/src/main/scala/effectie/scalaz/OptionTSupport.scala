package effectie.scalaz

import scalaz._
import Scalaz._

trait OptionTSupport {

  def optionTEffect[F[_] : EffectConstructor, A](a: => Option[A]): OptionT[F, A] =
    OptionT(EffectConstructor[F].effectOf(a))

  def optionTPureEffect[F[_] : EffectConstructor, A](a: Option[A]): OptionT[F, A] =
    OptionT(EffectConstructor[F].pureEffect(a))

  def optionTLiftEffect[F[_] : EffectConstructor : Functor, A](a: => A): OptionT[F, A] =
    OptionT(EffectConstructor[F].effectOf(a).map(_.some))

  def optionTLiftPureEffect[F[_] : EffectConstructor, A](a: A): OptionT[F, A] =
    OptionT(EffectConstructor[F].pureEffect(a.some))

  def optionTLiftF[F[_] : EffectConstructor : Functor, A](fa: F[A]): OptionT[F, A] =
    OptionT(fa.map(_.some))

}

object OptionTSupport extends OptionTSupport
