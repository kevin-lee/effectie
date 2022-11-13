package effectie.resource.data

/** @author Kevin Lee
  * @since 2022-11-06
  */

import TestResource._

final class TestResource(
  private var _content: Vector[String],
  private var _closeStatus: CloseStatus,
) // scalafix:ok DisableSyntax.var
    extends AutoCloseable {

  def content: Vector[String] = _content

  def closeStatus: CloseStatus = _closeStatus

  def write(s: String): Unit = {
    _content = _content :+ s
    ()
  }

  override def close(): Unit = {
    _closeStatus = CloseStatus.closed
    ()
  }
}

object TestResource {
  def apply(): TestResource = new TestResource(Vector.empty, CloseStatus.notClosed)

  sealed trait CloseStatus

  object CloseStatus {
    case object NotClosed extends CloseStatus

    case object Closed extends CloseStatus

    def notClosed: CloseStatus = NotClosed

    def closed: CloseStatus = Closed
  }
}
