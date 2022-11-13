package effectie.resource.data

/** @author Kevin Lee
  * @since 2022-11-13
  */
object TestErrors {
  final case class TestException(n: Int) extends RuntimeException
}
