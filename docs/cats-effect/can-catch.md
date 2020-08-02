---
id: can-catch
title: "CanCatch - Cats"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## CanCatch
`CanCatch` lets you catch `NonFatal` `Throwable` in the `F[A]`
 and turned it into `F[Either[Throwable, A]]`. It takes a function from `Throwable` to your own error type, yet it can handle only `NonFatal` ones as already mentioned.
 
```scala
trait CanCatch[F[_]] {
  def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Either[A, B]]

  def catchNonFatalEither[A, B](fab: => F[Either[A, B]])(f: Throwable => A): F[Either[A, B]]

  def catchNonFatalEitherT[A, B](fab: => EitherT[A, B])(f: Throwable => A): EitherT[A, B]
}
```

## CanCatch.catchNonFatal
 
### How to Use

<Tabs
  groupId="effects"
  defaultValue="io"
  values={[
    {label: 'IO', value: 'io'},
    {label: 'Future', value: 'future'},
    {label: 'Id', value: 'id'},
  ]}>
  <TabItem value="io">

```scala mdoc:reset-object
import cats.effect._

import effectie.cats._

val fa = CanCatch[IO].catchNonFatal(
    IO(throw new RuntimeException("Something's wrong!"))
  )(identity)

fa.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.cats._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = CanCatch[Future].catchNonFatal(
    Future(throw new RuntimeException("Something's wrong!"))
  )(identity)

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import cats._

import effectie.cats._

CanCatch[Id].catchNonFatal(
    throw new RuntimeException("Something's wrong!")
  )(identity)
```

  </TabItem>
</Tabs>

### Happy Path Example
<Tabs
  groupId="effects"
  defaultValue="io"
  values={[
    {label: 'IO', value: 'io'},
    {label: 'Future', value: 'future'},
    {label: 'Id', value: 'id'},
  ]}>
  <TabItem value="io">

```scala mdoc:reset-object
import cats._
import cats.implicits._
import cats.effect._

import effectie.cats._
import effectie.Effectful._

object BadApp {
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2
}

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

object MyService {

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(BadApp.doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)
}

object MyApp {
  def main(args: Array[String]): Unit =
    MyService.doSomething[IO](1)
      .unsafeRunSync() match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(MyError.NonFatalThrowable(a)) =>
          println(s"Result: Failed with $a")
      }
    
}

MyApp.main(Array.empty)
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

object BadApp {
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2
}

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

object MyService {

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(BadApp.doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)
}

object MyApp {
  def main(args: Array[String]): Unit = {
    val fa = MyService.doSomething[Future](1)
    fa.onComplete {
      case Success(Right(b)) =>
        println(s"Result is $b")
      case Success(Left(MyError.NonFatalThrowable(a))) =>
        println(s"Result: Failed with $a")
      case Failure(error) =>
        println(s"Failed! $error")
    }
    Await.ready(fa, 1.second)
    ()
  }
    
}

MyApp.main(Array.empty)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._

object BadApp {
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2
}

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

object MyService {

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(BadApp.doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)
}

object MyApp {
  def main(args: Array[String]): Unit =
    MyService.doSomething[Id](1) match {
      case Right(b) =>
        println(s"Result is $b")
      case Left(MyError.NonFatalThrowable(a)) =>
        println(s"Result: Failed with $a")
    }
    
}

MyApp.main(Array.empty)
```

  </TabItem>
</Tabs>

### Unhappy Path Example
<Tabs
  groupId="effects"
  defaultValue="io"
  values={[
    {label: 'IO', value: 'io'},
    {label: 'Future', value: 'future'},
    {label: 'Id', value: 'id'},
  ]}>
  <TabItem value="io">

```scala mdoc:reset-object
import cats._
import cats.implicits._
import cats.effect._

import effectie.cats._
import effectie.Effectful._

object BadApp {
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2
}

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

object MyService {

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(BadApp.doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)
}

object MyApp {
  def main(args: Array[String]): Unit = {
    val fa = MyService.doSomething[IO](-101)
    fa.unsafeRunSync() match {
      case Right(b) =>
        println(s"Result is $b")
      case Left(MyError.NonFatalThrowable(a)) =>
        println(s"Result: Failed with $a")
    }
  }
}

MyApp.main(Array.empty)
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._
import scala.util.{Success, Failure}

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

object BadApp {
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2
}

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

object MyService {

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(BadApp.doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)
}

object MyApp {
  def main(args: Array[String]): Unit = {
    val fa = MyService.doSomething[Future](-101)
    fa.onComplete {
      case Success(Right(b)) =>
        println(s"Result is $b")
      case Success(Left(MyError.NonFatalThrowable(a))) =>
        println(s"Result: Failed with $a")
      case Failure(error) =>
        println(s"Failed! $error")
    }
    Await.ready(fa, 1.second)
    ()
  }
    
}

MyApp.main(Array.empty)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._

object BadApp {
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2
}

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

object MyService {

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(BadApp.doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)
}

object MyApp {
  def main(args: Array[String]): Unit =
    MyService.doSomething[Id](-101) match {
      case Right(b) =>
        println(s"Result is $b")
      case Left(MyError.NonFatalThrowable(a)) =>
        println(s"Result: Failed with $a")
    }
    
}

MyApp.main(Array.empty)
```

  </TabItem>
</Tabs>

## CanCatch.catchNonFatalEither

## CanCatch.catchNonFatalEitherT

