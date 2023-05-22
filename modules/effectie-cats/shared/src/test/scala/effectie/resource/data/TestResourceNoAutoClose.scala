package effectie.resource.data

/** @author Kevin Lee
  * @since 2022-11-06
  */

import effectie.resource.data.TestableResource.CloseStatus

final class TestResourceNoAutoClose(
  private var _content: Vector[String],
  private var _closeStatus: CloseStatus,
) // scalafix:ok DisableSyntax.var
    extends TestableResource {

  override def content: Vector[String] = _content

  override def closeStatus: CloseStatus = _closeStatus

  override def write(s: String): Unit = {
    _content = _content :+ s
    ()
  }

  def release(): Unit = {
    _closeStatus = CloseStatus.closed
    ()
  }
}

object TestResourceNoAutoClose {
  def apply(): TestResourceNoAutoClose = new TestResourceNoAutoClose(Vector.empty, CloseStatus.notClosed)

}
