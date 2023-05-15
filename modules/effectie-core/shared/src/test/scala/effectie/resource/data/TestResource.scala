package effectie.resource.data

/** @author Kevin Lee
  * @since 2022-11-06
  */

import effectie.resource.data.TestableResource.CloseStatus

final case class TestResource private (
  private var _content: Vector[String],
  private var _closeStatus: CloseStatus,
) // scalafix:ok DisableSyntax.var
    extends TestableResource
    with AutoCloseable {

  override def content: Vector[String] = _content

  override def closeStatus: CloseStatus = _closeStatus

  override def write(s: String): Unit = {
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

  def withContent(content: Vector[String]): TestResource = new TestResource(content, CloseStatus.notClosed)

}
