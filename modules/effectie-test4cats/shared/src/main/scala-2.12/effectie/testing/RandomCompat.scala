package effectie.testing

import scala.util.Random

/** @author Kevin Lee
  * @since 2025-09-15
  */
trait RandomCompat {
  implicit def randomOps(r: Random): RandomCompat.RandomOps = new RandomCompat.RandomOps(r)
}
object RandomCompat {
  final class RandomOps(private val r: Random) extends AnyVal {

    def nextLong(n: Long): Long = {
      if (n <= 0L) throw new IllegalArgumentException("n must be positive") // scalafix:ok DisableSyntax.throw
      var u   = r.nextLong() >>> 1 // 63-bit non-negative
      var res = u % n

      while (u - res + (n - 1) < 0L) { // scalafix:ok DisableSyntax.while
        u = r.nextLong() >>> 1
        res = u % n
      }
      res
    }
  }
}
