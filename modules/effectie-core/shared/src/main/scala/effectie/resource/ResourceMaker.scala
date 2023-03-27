package effectie.resource

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

/** @author Kevin Lee
  * @since 2023-01-14
  */
trait ResourceMaker[F[*]] {
  def forAutoCloseable[A <: AutoCloseable](fa: F[A]): ReleasableResource[F, A]

  def make[A](fa: => F[A])(release: A => F[Unit]): ReleasableResource[F, A]

  def pure[A](a: A): ReleasableResource[F, A]

  def eval[A](fa: F[A]): ReleasableResource[F, A]
}
object ResourceMaker {
  def apply[F[*]: ResourceMaker]: ResourceMaker[F] = implicitly[ResourceMaker[F]]

  val usingResourceMaker: ResourceMaker[Try] = new UsingResourceMaker

  private final class UsingResourceMaker extends ResourceMaker[Try] {
    override def forAutoCloseable[A <: AutoCloseable](fa: Try[A]): ReleasableResource[Try, A] =
      ReleasableResource.usingResourceFromTry(fa)

    override def make[A](fa: => Try[A])(release: A => Try[Unit]): ReleasableResource[Try, A] =
      ReleasableResource.makeTry(fa)(release)

    override def pure[A](a: A): ReleasableResource[Try, A] = make(Try(a))(_ => Try(()))

    override def eval[A](fa: Try[A]): ReleasableResource[Try, A] = make(fa)(_ => Try(()))
  }

  def futureResourceMaker(implicit ec: ExecutionContext): ResourceMaker[Future] =
    new FutureResourceMaker

  private final class FutureResourceMaker(implicit ec: ExecutionContext) extends ResourceMaker[Future] {
    override def forAutoCloseable[A <: AutoCloseable](fa: Future[A]): ReleasableResource[Future, A] =
      ReleasableResource.futureResource(fa)

    override def make[A](fa: => Future[A])(release: A => Future[Unit]): ReleasableResource[Future, A] =
      ReleasableResource.makeFuture(fa)(release)

    override def pure[A](a: A): ReleasableResource[Future, A] = make(Future.successful(a))(_ => Future.unit)

    override def eval[A](fa: Future[A]): ReleasableResource[Future, A] = make(fa)(_ => Future.unit)
  }
}
