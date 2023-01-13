package effectie.resource

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-01-14
  */
trait ResourceMaker[F[*]] {
  def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A]
}
object ResourceMaker {
  def apply[F[*]: ResourceMaker]: ResourceMaker[F] = implicitly[ResourceMaker[F]]

  def usingResourceMaker: ResourceMaker[Try] = new UsingResourceMaker
  private final class UsingResourceMaker extends ResourceMaker[Try] {
    override def forAutoCloseable[A <: AutoCloseable](fa: Try[A]): ReleasableResource[Try, A] =
      ReleasableResource.usingResourceFromTry(fa)
  }

  def futureResourceMaker(implicit ec: ExecutionContext): ResourceMaker[Future] =
    new FutureResourceMaker

  private final class FutureResourceMaker(implicit ec: ExecutionContext) extends ResourceMaker[Future] {
    override def forAutoCloseable[A <: AutoCloseable](fa: Future[A]): ReleasableResource[Future, A] =
      ReleasableResource.futureResource(fa)
  }
}
