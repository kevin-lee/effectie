package effectie.resource

import cats.effect.{Bracket, BracketThrow, ExitCase, Resource}
import cats.syntax.all._

/** Interop and interpretation of `ReleasableResource` through cats-effect 2's `Resource`.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object Ce2UseResource {

  def useResource[F[*]: BracketThrow]: UseResource[F] = new UseResource[F] {

    override def use[A, B](resource: ReleasableResource[F, A])(f: A => F[B]): F[B] =
      toCatsEffectResource(resource).use(f)

    override def unitOf: F[Unit] = Bracket[F, Throwable].unit
  }

  /** Translates the pure `ReleasableResource` description into a real cats-effect 2 `Resource`, so running it gets
    * cats-effect's bracketing and cancellation safety unchanged.
    */
  def toCatsEffectResource[F[*]: BracketThrow, A](resource: ReleasableResource[F, A]): Resource[F, A] =
    resource match {
      case ReleasableResource.Pure(a) =>
        Resource.pure(a)

      case ReleasableResource.Eval(fa) =>
        Resource.eval(fa())

      case ReleasableResource.Fail(err) =>
        Resource.eval(Bracket[F, Throwable].raiseError[A](err))

      case ReleasableResource.Allocate(acquire, release) =>
        Resource.makeCase(acquire()) { (a, exitCase) =>
          release(a, fromCe2ExitCase(exitCase))
        }

      case ReleasableResource.Bind(source, nextF) =>
        toCatsEffectResource(source).flatMap(s => toCatsEffectResource(nextF(s)))

      case ReleasableResource.HandleErrorWith(source, handler) =>
        toCatsEffectResource(source).handleErrorWith((err: Throwable) => toCatsEffectResource(handler(err)))

      case ReleasableResource.OnFinalizeCase(source, finalizer) =>
        toCatsEffectResource(source).onFinalizeCase(exitCase => finalizer(fromCe2ExitCase(exitCase)))
    }

  /** Wraps an existing cats-effect 2 `Resource` as a `ReleasableResource`.
    *
    * Note: cats-effect 2's `allocated` does not expose an exit case, so the wrapped resource's finalizer runs the same
    * way regardless of how the scope ends.
    */
  def fromCatsEffectResource[F[*]: BracketThrow, A](underlying: Resource[F, A]): ReleasableResource[F, A] =
    ReleasableResource
      .makeCase(underlying.allocated) { (allocated, _) =>
        allocated match {
          case (_, finalizer) => finalizer
        }
      }
      .map { case (a, _) => a }

  private def fromCe2ExitCase(exitCase: ExitCase[Throwable]): ReleasableResource.ExitCase =
    exitCase match {
      case ExitCase.Completed => ReleasableResource.ExitCase.completed
      case ExitCase.Error(err) => ReleasableResource.ExitCase.errored(err)
      case ExitCase.Canceled => ReleasableResource.ExitCase.canceled
    }

}
