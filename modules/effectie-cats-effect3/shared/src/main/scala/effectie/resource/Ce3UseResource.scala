package effectie.resource

import cats.effect.Resource
import cats.effect.kernel.MonadCancelThrow

/** Interop and interpretation of `ReleasableResource` through cats-effect 3's `Resource`.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
object Ce3UseResource {

  def useResource[F[*]: MonadCancelThrow]: UseResource[F] = new UseResource[F] {

    override def use[A, B](resource: ReleasableResource[F, A])(f: A => F[B]): F[B] =
      toCatsEffectResource(resource).use(f)

    override def unitOf: F[Unit] = MonadCancelThrow[F].unit
  }

  /** Translates the pure `ReleasableResource` description into a real cats-effect `Resource`, so running it gets
    * cats-effect's bracketing and cancellation safety unchanged.
    */
  def toCatsEffectResource[F[*]: MonadCancelThrow, A](resource: ReleasableResource[F, A]): Resource[F, A] =
    resource match {
      case ReleasableResource.Pure(a) =>
        Resource.pure(a)

      case ReleasableResource.Eval(fa) =>
        Resource.eval(fa())

      case ReleasableResource.Fail(err) =>
        Resource.eval(MonadCancelThrow[F].raiseError[A](err))

      case ReleasableResource.Allocate(acquire, release) =>
        Resource.makeCase(acquire()) { (a, exitCase) =>
          release(a, fromCe3ExitCase(exitCase))
        }

      case ReleasableResource.Bind(source, nextF) =>
        toCatsEffectResource(source).flatMap(s => toCatsEffectResource(nextF(s)))

      case ReleasableResource.HandleErrorWith(source, handler) =>
        toCatsEffectResource(source).handleErrorWith((err: Throwable) => toCatsEffectResource(handler(err)))

      case ReleasableResource.OnFinalizeCase(source, finalizer) =>
        toCatsEffectResource(source).onFinalizeCase(exitCase => finalizer(fromCe3ExitCase(exitCase)))
    }

  /** Wraps an existing cats-effect `Resource` as a `ReleasableResource`.
    *
    * The underlying resource is allocated through `allocatedCase` inside the acquisition step, so when run back
    * through cats-effect (the instances from `effectie.instances.ce3.resource` / `effectie.instances.ce3.f.resource`),
    * acquisition remains uncancelable and the finalizer is registered atomically.
    */
  def fromCatsEffectResource[F[*]: MonadCancelThrow, A](underlying: Resource[F, A]): ReleasableResource[F, A] =
    ReleasableResource
      .makeCase(underlying.allocatedCase) { (allocated, exitCase) =>
        allocated match {
          case (_, finalizer) => finalizer(toCe3ExitCase(exitCase))
        }
      }
      .map { case (a, _) => a }

  private def fromCe3ExitCase(exitCase: Resource.ExitCase): ReleasableResource.ExitCase =
    exitCase match {
      case Resource.ExitCase.Succeeded => ReleasableResource.ExitCase.completed
      case Resource.ExitCase.Errored(err) => ReleasableResource.ExitCase.errored(err)
      case Resource.ExitCase.Canceled => ReleasableResource.ExitCase.canceled
    }

  private def toCe3ExitCase(exitCase: ReleasableResource.ExitCase): Resource.ExitCase =
    exitCase match {
      case ReleasableResource.ExitCase.Completed => Resource.ExitCase.Succeeded
      case ReleasableResource.ExitCase.Errored(err) => Resource.ExitCase.Errored(err)
      case ReleasableResource.ExitCase.Canceled => Resource.ExitCase.Canceled
    }

}
