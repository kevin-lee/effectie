package effectie.testing

import scala.util.Random

object RandomGens {
  val AlphaChars: Seq[Char]    = ('a' to 'z') ++ ('A' to 'Z')
  val AlphaNumChars: Seq[Char] = AlphaChars ++ ('0' to '9')

  def genRandomIntWithMinMax(min: Int, max: Int): Int = {
    if (min > max) {
      throw new IllegalArgumentException(s"min ($min) must be less than or equal to max ($max)")
    } else {
      val bound = max.toLong - min.toLong + 1L
      if (bound <= Int.MaxValue) {
        Random.nextInt(bound.toInt) + min
      } else {
        (Random.nextInt() & Int.MaxValue) + min
      }
    }
  }

  def genRandomInt(): Int = genRandomIntWithMinMax(0, Int.MaxValue)

  def genRandomLongWithMinMax(min: Long, max: Long): Long = {
    if (min > max) {
      throw new IllegalArgumentException(s"min ($min) must be less than or equal to max ($max)")
    } else {
      val bound = max - min + 1L
      if (bound <= Long.MaxValue) {
        Random.nextLong(bound) + min
      } else {
        (Random.nextLong() & Long.MaxValue) + min
      }
    }
  }

  def genAlphaString(length: Int): String =
    (1 to length).map(_ => AlphaChars(Random.nextInt(AlphaChars.length))).mkString

  def genAlphaStringList(length: Int, listSize: Int): List[String] =
    (1 to listSize).map(_ => genAlphaString(length)).toList

  def genAlphaNumericString(length: Int): String =
    (1 to length).map(_ => AlphaNumChars(Random.nextInt(AlphaNumChars.length))).mkString

  def genAlphaNumericStringList(length: Int, listSize: Int): List[String] =
    (1 to listSize).map(_ => genAlphaNumericString(length)).toList

  def genBoolean(): Boolean = genRandomIntWithMinMax(0, 1) == 0
}
