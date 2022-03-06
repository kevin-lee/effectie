package effectie.testing.cats

import cats.Monad
import hedgehog.{Gen, Range}

/** @author Kevin Lee
  * @since 2021-08-04
  */
object Gens {

  def genInt(from: Int, to: Int): Gen[Int] =
    Gen.int(Range.linear(from, to))

  def genIntFromMinToMax: Gen[Int] = Gens.genInt(Int.MinValue, Int.MaxValue)

  def genIntToInt: Gen[Int => Int] =
    /* It has some hard-coded functions for now until Hedgehog has Gen[A => B]
     * Reference: https://github.com/hedgehogqa/scala-hedgehog/issues/90
     */
    Gen.element1[Int => Int](
      identity[Int],
      x => x + x,
      x => x - x,
      x => x * x,
      x => x + 100,
      x => x - 100,
      x => x * 100
    )

  def genAToMonadA[F[*]: Monad, A](genF: Gen[A => A]): Gen[A => F[A]] =
    genF.map(f => x => Monad[F].pure(f(x)))

  def genFA[F[*]: Monad, A](genA: Gen[A]): Gen[F[A]] =
    genA.map(a => Monad[F].pure(a))

}
