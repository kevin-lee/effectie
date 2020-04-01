package effectie.cats

import cats.Functor
import cats.data.EitherT

trait EitherTSupport {

  def eitherTEffect[F[_] : EffectConstructor, A, B](ab: => Either[A, B]): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].effectOf(ab))

  def eitherTPureEffect[F[_] : EffectConstructor, A, B](ab: Either[A, B]): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].pureEffect(ab))

  def eitherTLiftEffect[F[_] : EffectConstructor : Functor, A, B](b: => B): EitherT[F, A, B] =
    EitherT.liftF[F, A, B](EffectConstructor[F].effectOf(b))

  def eitherTLiftPureEffect[F[_] : EffectConstructor : Functor, A, B](b: B): EitherT[F, A, B] =
    EitherT.liftF[F, A, B](EffectConstructor[F].pureEffect(b))

  def eitherTLiftF[F[_] : EffectConstructor : Functor, A, B](fb: F[B]): EitherT[F, A, B] =
    EitherT.liftF[F, A, B](fb)

}

object EitherTSupport extends  EitherTSupport
