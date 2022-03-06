package effectie

import scala.util.control.ControlThrowable

/** @author Kevin Lee
  * @since 2020-07-31
  */
final case class SomeControlThrowable(message: String) extends ControlThrowable(message)
