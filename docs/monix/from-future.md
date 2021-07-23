---
id: from-future
title: "FromFuture"
---
## FromFuture

`FromFuture` is a typeclass to convert `scala.concurrent.Future` to an effect, `F[_]`. So if there are some APIs returning `Future`, it can be converted to `F[_]`.

There are three `FromFuture` instances available.
* `FromFuture` for `monix.eval.Task`
* `FromFuture` for `scala.concurrent.Future`
* `FromFuture` for `cats.Id`
```scala
trait FromFuture[F[_]] {
  def toEffect[A](future: => Future[A]): F[A]
}
```


## FromFuture.toEffect

```scala mdoc:reset-object
import cats._
import cats.syntax.all._
import monix.eval._

import effectie.monix._
import effectie.monix.Effectful._

import effectie.concurrent.ExecutorServiceOps

import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object MyApp {

  def foo(n: Int)(implicit ec: ExecutionContext): Future[Int] =
    Future(n + 100)

  def bar[F[_]: Fx](n: Int): F[Int] =
    pureOf(n * 2)

  def baz[F[_]: Monad: Fx: FromFuture](n: Int)(implicit ec: ExecutionContext): F[Int] =
    for {
      a <- FromFuture[F].toEffect(foo(n))
      b <- bar[F](a)
    } yield b

}

val executorService: ExecutorService =
  Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors() >> 1)
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

import monix.execution.Scheduler.Implicits.global
try {
  println(MyApp.baz[Task](1).runSyncUnsafe())
} finally {
  ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
}
```
