package effectie.scalaz

import scalaz._
import Scalaz._

trait OptionTSupport {

  def optionTEffectOf[F[_]: EffectConstructor, A](a: => Option[A]): OptionT[F, A] =
    OptionT(EffectConstructor[F].effectOf(a))

  def optionTEffectOfPure[F[_]: EffectConstructor, A](a: Option[A]): OptionT[F, A] =
    OptionT(EffectConstructor[F].effectOfPure(a))

  def optionTLiftEffectOf[F[_]: EffectConstructor: Functor, A](a: => A): OptionT[F, A] =
    OptionT(EffectConstructor[F].effectOf(a).map(_.some))

  def optionTLiftEffectOfPure[F[_]: EffectConstructor, A](a: A): OptionT[F, A] =
    OptionT(EffectConstructor[F].effectOfPure(a.some))

  def optionTLiftF[F[_]: EffectConstructor: Functor, A](fa: F[A]): OptionT[F, A] =
    OptionT(fa.map(_.some))

}

object OptionTSupport extends OptionTSupport
