package effectie.testing.cats

import cats.{Applicative, Eq, Functor, Monad}

/** @author Kevin Lee
  * @since 2021-08-04
  */
object Laws {
  trait FunctorLaws {
    /* Functors must preserve identity morphisms
     * fmap id = id
     */
    def identity[F[*], A](fa: F[A])(
      implicit F: Functor[F],
      FA: Eq[F[A]],
    ): Boolean =
      FA.eqv(
        F.map(fa)(scala.Predef.identity),
        fa,
      )

    /* Functors preserve composition of morphisms
     * fmap (f . g)  ==  fmap f . fmap g
     */
    def composition[F[*], A, B, C](fa: F[A], f: B => C, g: A => B)(
      implicit F: Functor[F],
      FC: Eq[F[C]],
    ): Boolean =
      FC.eqv(
        F.map(fa)(f compose g),
        F.map(F.map(fa)(g))(f),
      )
  }
  object FunctorLaws extends FunctorLaws

  trait ApplicativeLaws extends FunctorLaws {
    /* Identity
     * pure id <*> v = v
     */
    def identityAp[F[*], A](fa: => F[A])(
      implicit F: Applicative[F],
      FA: Eq[F[A]],
    ): Boolean =
      FA.eqv(
        F.ap[A, A](F.pure(scala.Predef.identity))(fa),
        fa,
      )

    /* Homomorphism
     * pure f <*> pure x = pure (f x)
     */
    def homomorphism[F[*], A, B](f: A => B, a: => A)(
      implicit F: Applicative[F],
      FB: Eq[F[B]],
    ): Boolean =
      FB.eqv(
        F.ap(F.pure(f))(F.pure(a)),
        F.pure(f(a)),
      )

    /* Interchange
     * u <*> pure y = pure ($ y) <*> u
     */
    def interchange[F[*], A, B](a: => A, f: F[A => B])(
      implicit F: Applicative[F],
      FB: Eq[F[B]],
    ): Boolean =
      FB.eqv(
        F.ap[A, B](f)(F.pure(a)),
        F.ap[A => B, B](F.pure(g => g(a)))(f),
      )

    /* Composition
     * pure (.) <*> u <*> v <*> w = u <*> (v <*> w)
     */
    def compositionAp[F[*], A, B, C](fa: F[A], f: F[B => C], g: F[A => B])(
      implicit F: Applicative[F],
      FC: Eq[F[C]],
    ): Boolean =
      FC.eqv(
        F.ap[A, C](
          F.ap[A => B, A => C](
            F.ap[B => C, (A => B) => (A => C)](
              F.pure(bc => ab => bc compose ab)
            )(f)
          )(g)
        )(fa),
        F.ap[B, C](f)(
          F.ap[A, B](g)(fa)
        ),
      )
  }
  object ApplicativeLaws extends ApplicativeLaws

  trait MonadLaws extends ApplicativeLaws {
    /*
     * return a >>= f === f a
     */
    def leftIdentity[F[*], A, B](a: A, f: A => F[B])(
      implicit F: Monad[F],
      FB: Eq[F[B]],
    ): Boolean =
      FB.eqv(
        F.flatMap(F.pure(a))(f),
        f(a),
      )

    /*
     * m >>= return === m
     */
    def rightIdentity[F[*], A](fa: F[A])(
      implicit F: Monad[F],
      FA: Eq[F[A]],
    ): Boolean =
      FA.eqv(
        F.flatMap(fa)(F.pure(_: A)),
        fa,
      )

    /*
     * (m >>= f) >>= g === m >>= (\x -> f x >>= g)
     */
    def associativity[F[*], A, B, C](fa: F[A], f: A => F[B], g: B => F[C])(
      implicit F: Monad[F],
      FC: Eq[F[C]],
    ): Boolean =
      FC.eqv(
        F.flatMap(F.flatMap(fa)(f))(g),
        F.flatMap(fa)(x => F.flatMap(f(x))(g)),
      )
  }
  object MonadLaws extends MonadLaws

}
