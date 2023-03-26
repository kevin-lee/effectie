package effectie.resource.data

import TestableResource.CloseStatus

/** @author Kevin Lee
  * @since 2023-03-26
  */
trait TestableResource {
  def content: Vector[String]

  def closeStatus: CloseStatus

  def write(s: String): Unit
}
object TestableResource {
  sealed trait CloseStatus

  object CloseStatus {
    case object NotClosed extends CloseStatus

    case object Closed extends CloseStatus

    def notClosed: CloseStatus = NotClosed

    def closed: CloseStatus = Closed
  }
}
