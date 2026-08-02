package effectie.resource

import cats.MonadThrow

import scala.annotation.implicitNotFound
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.control.NonFatal

/** The capability of running a [[ReleasableResource]] in `F`: acquire, run the given function, then release in LIFO
  * order on success and on error.
  *
  * There is exactly one instance per effect system. `Try` and `Future` instances live in the companion object, so they
  * are found with no import. cats-effect and Monix instances are provided by their effectie modules (see the
  * implicitNotFound message below) and interpret the resource through `cats.effect.Resource`, preserving cancellation
  * safety — which is also why there is deliberately no generic fallback instance here.
  *
  * @author Kevin Lee
  * @since 2026-08-02
  */
@implicitNotFound(
  """
  Could not find an implicit UseResource[${F}] required to run ReleasableResource[${F}, A].
  ---
  For scala.util.Try and scala.concurrent.Future, the instances are provided automatically.
  (Future requires an implicit scala.concurrent.ExecutionContext in scope.)

  If you use IO from cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.resource._

    // for Scala 3
    import effectie.instances.ce3.resource.given
    // or
    import effectie.instances.ce3.resource.ioUseResource

    // or for any F[_] with MonadCancelThrow
    import effectie.instances.ce3.f.resource._

    // for Scala 3
    import effectie.instances.ce3.f.resource.given

  If you use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.resource._

    // for Scala 3
    import effectie.instances.ce2.resource.given
    // or
    import effectie.instances.ce2.resource.ioUseResource

    // or for any F[_] with BracketThrow
    import effectie.instances.ce2.f.resource._

    // for Scala 3
    import effectie.instances.ce2.f.resource.given

  If you use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.resource._

    // for Scala 3
    import effectie.instances.monix3.resource.given
    // or
    import effectie.instances.monix3.resource.taskUseResource
  ---
  """
)
trait UseResource[F[*]] {

  def use[A, B](resource: ReleasableResource[F, A])(f: A => F[B]): F[B]

  def unitOf: F[Unit]

}

object UseResource {

  def apply[F[*]: UseResource]: UseResource[F] = implicitly[UseResource[F]]

  implicit val tryUseResource: UseResource[Try] = new MonadThrowUseResource[Try]

  implicit def futureUseResource(implicit ec: ExecutionContext): UseResource[Future] =
    new MonadThrowUseResource[Future]

  /** A generic interpreter for any `F` with a lawful `MonadThrow` (so a stack-safe `tailRecM`).
    *
    * It guarantees release on success and on error but cannot know about cancellation, so it must not be used for
    * cancellable effect systems like cats-effect `IO` or Monix `Task` — their instances translate to
    * `cats.effect.Resource` instead.
    */
  private[resource] final class MonadThrowUseResource[F[*]](implicit MT: MonadThrow[F]) extends UseResource[F] {

    import ReleasableResource._

    override def unitOf: F[Unit] = MT.unit

    override def use[A, B](resource: ReleasableResource[F, A])(f: A => F[B]): F[B] = {

      def safely[X](fx: => F[X]): F[X] =
        try fx
        catch {
          case NonFatal(err) => MT.raiseError[X](err)
        }

      def runFinalizers(finalizers: List[ExitCase => F[Unit]], exitCase: ExitCase): F[List[Throwable]] =
        finalizers.foldLeft(MT.pure(List.empty[Throwable])) { (accF, finalizer) =>
          MT.flatMap(accF) { acc =>
            MT.map(MT.attempt(safely(finalizer(exitCase)))) {
              case Left(err) => acc :+ err
              case Right(_) => acc
            }
          }
        }

      def suppressOnto(error: Throwable, suppressed: List[Throwable]): Throwable = {
        suppressed.foreach(err => if (err ne error) error.addSuppressed(err) else ())
        error
      }

      def finishSuccess(value: Any, finalizers: List[ExitCase => F[Unit]]): F[B] =
        MT.flatMap(MT.attempt(safely(f(value.asInstanceOf[A])))) { // scalafix:ok DisableSyntax.asInstanceOf
          result =>
            val exitCase = result.fold(err => ExitCase.errored(err), _ => ExitCase.completed)
            MT.flatMap(runFinalizers(finalizers, exitCase)) { finalizerErrors =>
              result match {
                case Right(b) =>
                  finalizerErrors match {
                    case Nil => MT.pure(b)
                    case first :: rest => MT.raiseError[B](suppressOnto(first, rest))
                  }
                case Left(useError) =>
                  MT.raiseError[B](suppressOnto(useError, finalizerErrors))
              }
            }
        }

      def finishError(error: Throwable, finalizers: List[ExitCase => F[Unit]]): F[B] =
        MT.flatMap(runFinalizers(finalizers, ExitCase.errored(error))) { finalizerErrors =>
          MT.raiseError[B](suppressOnto(error, finalizerErrors))
        }

      val initial: St[F] = St(Phase.Expand[F](resource), List.empty, List.empty)

      MT.tailRecM[St[F], B](initial) { state =>
        state.phase match {

          case Phase.Expand(node) =>
            node match {
              case Pure(a) =>
                MT.pure(Left(state.copy(phase = Phase.Produced[F](a))))

              case Eval(fa) =>
                MT.map(MT.attempt(safely(fa()))) {
                  case Right(a) => Left(state.copy(phase = Phase.Produced[F](a)))
                  case Left(err) => Left(state.copy(phase = Phase.Raised[F](err)))
                }

              case Fail(err) =>
                MT.pure(Left(state.copy(phase = Phase.Raised[F](err))))

              case Allocate(acquire, release) =>
                MT.map(MT.attempt(safely(acquire()))) {
                  case Right(a) =>
                    Left(
                      St(
                        Phase.Produced[F](a),
                        state.control,
                        ((exitCase: ExitCase) => release(a, exitCase)) :: state.finalizers,
                      )
                    )
                  case Left(err) =>
                    Left(state.copy(phase = Phase.Raised[F](err)))
                }

              case Bind(source, nextF) =>
                MT.pure(
                  Left(
                    St(
                      Phase.Expand[F](source),
                      Control.Cont[F](
                        nextF.asInstanceOf[Any => ReleasableResource[F, Any]] // scalafix:ok DisableSyntax.asInstanceOf
                      ) :: state.control,
                      state.finalizers,
                    )
                  )
                )

              case HandleErrorWith(source, handler) =>
                MT.pure(
                  Left(
                    St(
                      Phase.Expand[F](source),
                      Control.Handler[F](
                        handler.asInstanceOf[
                          Throwable => ReleasableResource[F, Any]
                        ] // scalafix:ok DisableSyntax.asInstanceOf
                      ) :: state.control,
                      state.finalizers,
                    )
                  )
                )

              case OnFinalizeCase(source, finalizer) =>
                MT.pure(
                  Left(
                    St(
                      Phase.Expand[F](source),
                      state.control,
                      finalizer :: state.finalizers,
                    )
                  )
                )
            }

          case Phase.Produced(value) =>
            state.control match {
              case Control.Cont(nextF) :: rest =>
                val nextPhase =
                  try Phase.Expand[F](nextF(value))
                  catch {
                    case NonFatal(err) => Phase.Raised[F](err)
                  }
                MT.pure(Left(St(nextPhase, rest, state.finalizers)))

              case Control.Handler(_) :: rest =>
                MT.pure(Left(St(Phase.Produced[F](value), rest, state.finalizers)))

              case Nil =>
                MT.map(finishSuccess(value, state.finalizers))(b => Right(b))
            }

          case Phase.Raised(error) =>
            state.control match {
              case Control.Cont(_) :: rest =>
                MT.pure(Left(St(Phase.Raised[F](error), rest, state.finalizers)))

              case Control.Handler(handler) :: rest =>
                val nextPhase =
                  try Phase.Expand[F](handler(error))
                  catch {
                    case NonFatal(err) => Phase.Raised[F](err)
                  }
                MT.pure(Left(St(nextPhase, rest, state.finalizers)))

              case Nil =>
                MT.map(finishError(error, state.finalizers))(b => Right(b))
            }
        }
      }
    }
  }

  private sealed trait Control[F[*]]
  private object Control {
    final case class Cont[F[*]](f: Any => ReleasableResource[F, Any]) extends Control[F]
    final case class Handler[F[*]](f: Throwable => ReleasableResource[F, Any]) extends Control[F]
  }

  private sealed trait Phase[F[*]]
  private object Phase {
    final case class Expand[F[*]](node: ReleasableResource[F, Any]) extends Phase[F]
    final case class Produced[F[*]](value: Any) extends Phase[F]
    final case class Raised[F[*]](error: Throwable) extends Phase[F]
  }

  private final case class St[F[*]](
    phase: Phase[F],
    control: List[Control[F]],
    finalizers: List[ReleasableResource.ExitCase => F[Unit]],
  )

}
