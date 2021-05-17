package effectie.testing

/** @author
  *   Kevin Lee
  * @since
  *   2021-05-16
  */
object tools {
  def dropResult[A](a: => A): Unit = {
    val _ = a
    ()
  }
}
