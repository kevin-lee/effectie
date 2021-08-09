package effectie.testing

import scalaz.{Applicative, Equal, Functor, Monad}

/** @author Kevin Lee
  * @since 2021-08-04
  */
object Laws {
  trait FunctorLaws {
    /* Functors must preserve identity morphisms
     * fmap id = id
     */
    def identity[F[_], A](fa: F[A])(
      implicit F: Functor[F],
      FA: Equal[F[A]]
    ): Boolean =
      FA.equal(
        F.map(fa)(scala.Predef.identity),
        fa
      )

    /* Functors preserve composition of morphisms
     * fmap (f . g)  ==  fmap f . fmap g
     */
    def composition[F[_]: Functor, A, B, C](fa: F[A], f: B => C, g: A => B)(
      implicit F: Functor[F],
      FC: Equal[F[C]]
    ): Boolean =
      FC.equal(
        F.map(fa)(f compose g),
        F.map(F.map(fa)(g))(f)
      )
  }
  object FunctorLaws extends FunctorLaws

  trait ApplicativeLaws  extends FunctorLaws {
    /* Identity
     * pure id <*> v = v
     */
    def identityAp[F[_]: Applicative, A](fa: => F[A])(
      implicit F: Functor[F],
      FA: Equal[F[A]]
    ): Boolean =
      FA.equal(
        Applicative[F].ap[A, A](fa)(Applicative[F].pure(scala.Predef.identity)),
        fa
      )

    /* Homomorphism
     * pure f <*> pure x = pure (f x)
     */
    def homomorphism[F[_]: Applicative, A, B](f: A => B, a: => A)(
      implicit F: Functor[F],
      FB: Equal[F[B]]
    ): Boolean =
      FB.equal(
        Applicative[F].ap(Applicative[F].pure(a))(Applicative[F].pure(f)),
        Applicative[F].pure(f(a))
      )

    /* Interchange
     * u <*> pure y = pure ($ y) <*> u
     */
    def interchange[F[_], A, B](a: => A, f: F[A => B])(
      implicit F: Applicative[F],
      FB: Equal[F[B]]
    ): Boolean =
      FB.equal(
        F.ap(F.pure(a))(f),
        F.ap(f)(F.pure(g => g(a)))
      )

    /* Composition
     * pure (.) <*> u <*> v <*> w = u <*> (v <*> w)
     */
    def compositionAp[F[_], A, B, C](fa: F[A], f: F[B => C], g: F[A => B])(
      implicit F: Applicative[F],
      FC: Equal[F[C]]
    ): Boolean =
      FC.equal(
        F.ap(fa)(
          F.ap(g)(
            F.ap(f)(
              F.pure(bc => ab => bc compose ab)
            )
          )
        ),
        F.ap(
          F.ap(fa)(g)
        )(f)
      )
  }
  object ApplicativeLaws extends ApplicativeLaws

  trait MonadLaws  extends ApplicativeLaws {
    /*
     * return a >>= f === f a
     */
    def leftIdentity[F[_], A, B](a: A, f: A => F[B])(
      implicit F: Monad[F],
      FB: Equal[F[B]]
    ): Boolean =
      FB.equal(
        F.bind(F.pure(a))(f),
        f(a)
      )

    /*
     * m >>= return === m
     */
    def rightIdentity[F[_], A](fa: F[A])(
      implicit F: Monad[F],
      FA: Equal[F[A]]
    ): Boolean =
      FA.equal(
        F.bind(fa)(F.pure(_: A)),
        fa
      )

    /*
     * (m >>= f) >>= g === m >>= (\x -> f x >>= g)
     */
    def associativity[F[_], A, B, C](fa: F[A], f: A => F[B], g: B => F[C])(
      implicit F: Monad[F],
      FC: Equal[F[C]]
    ): Boolean =
      FC.equal(
        F.bind(F.bind(fa)(f))(g),
        F.bind(fa)(x => F.bind(f(x))(g))
      )
  }
  object MonadLaws extends MonadLaws

}
