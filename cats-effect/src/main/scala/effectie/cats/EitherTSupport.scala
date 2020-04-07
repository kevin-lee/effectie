package effectie.cats

import cats.Functor
import cats.data.EitherT
import cats.implicits._

trait EitherTSupport {

  def eitherTEffect[F[_] : EffectConstructor, A, B](ab: => Either[A, B]): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].effectOf(ab))

  def eitherTEffectOfPure[F[_] : EffectConstructor, A, B](ab: Either[A, B]): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].effectOfPure(ab))

  def eitherTLiftEffect[F[_] : EffectConstructor : Functor, A, B](b: => B): EitherT[F, A, B] =
    EitherT.liftF[F, A, B](EffectConstructor[F].effectOf(b))

  def eitherTLiftEffectOfPure[F[_] : EffectConstructor, A, B](b: B): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].effectOfPure(b.asRight[A]))

  def eitherTLiftF[F[_] : EffectConstructor : Functor, A, B](fb: F[B]): EitherT[F, A, B] =
    EitherT.liftF[F, A, B](fb)

}

object EitherTSupport extends  EitherTSupport
