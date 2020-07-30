package effectie

import scala.util.control.ControlThrowable

/**
 * @author Kevin Lee
 * @since 2020-07-31
 */
@SuppressWarnings(Array("org.wartremover.warts.Null"))
class SomeControlThrowable(val message: String)
  extends Throwable(message, null, false, false)
     with ControlThrowable
