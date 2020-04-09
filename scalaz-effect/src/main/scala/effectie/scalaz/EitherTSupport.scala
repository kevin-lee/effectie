package effectie.scalaz

import scalaz.{EitherT, Functor, \/}

trait EitherTSupport {

  def eitherTEffectOf[F[_] : EffectConstructor, A, B](ab: => A \/ B): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].effectOf(ab))

  def eitherTEffectOfPure[F[_] : EffectConstructor, A, B](ab: A \/ B): EitherT[F, A, B] =
    EitherT(EffectConstructor[F].effectOfPure(ab))

  def eitherTLiftEffectOf[F[_] : EffectConstructor : Functor, A, B](b: => B): EitherT[F, A, B] =
    EitherT.rightT[F, A, B](EffectConstructor[F].effectOf(b))

  def eitherTLiftEffectOfPure[F[_] : EffectConstructor : Functor, A, B](b: B): EitherT[F, A, B] =
    EitherT.rightT[F, A, B](EffectConstructor[F].effectOfPure(b))

  def eitherTLiftF[F[_] : EffectConstructor : Functor, A, B](fb: F[B]): EitherT[F, A, B] =
    EitherT.rightT[F, A, B](fb)

}

object EitherTSupport extends  EitherTSupport
