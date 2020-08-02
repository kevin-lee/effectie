---
id: can-catch
title: "CanCatch - Cats"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## CanCatch
`CanCatch` lets you catch `NonFatal` `Throwable` in the `F[A]`
 and turned it into `F[Either[Throwable, A]]`. It takes a function from `Throwable` 
 to your own error type, yet it can handle only `NonFatal` ones as already mentioned.
 
```scala
trait CanCatch[F[_]] {
  def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[Either[A, B]]

  def catchNonFatalEither[A, B](fab: => F[Either[A, B]])(f: Throwable => A): F[Either[A, B]]

  def catchNonFatalEitherT[A, B](fab: => EitherT[A, B])(f: Throwable => A): EitherT[A, B]
}
```

## CanCatch.catchNonFatal
`CanCatch[F].catchNonFatal[A, B]` lets you catch `NonFatal` `Throwable` from `F[B]`
 and returns `F[Either[A, B]]`.

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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  }

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)

  def main(arg: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2


def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-101)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._


import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  }

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    try {
      val fa = doSomething[Future](-101)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-101)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>

## CanCatch.catchNonFatalEither
`CanCatch[F].catchNonFatalEither[A, B]` lets you catch `NonFatal` `Throwable` from `F[Either[A, B]]`
 and returns `F[Either[A, B]]`.

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

val fa = CanCatch[IO].catchNonFatalEither(
    IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
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

val fa = CanCatch[Future].catchNonFatalEither(
    Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
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

CanCatch[Id].catchNonFatalEither(
    (throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError

    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]
  
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEither(
      for {
        aOrB <- effectOfPure(divide100By(n))
        c <- effectOf(aOrB.map(b => doSomethingBad(b)))
      } yield c
    )(MyError.nonFatalThrowable)

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    try {
      val fa = doSomething[Future](1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError
    
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEither(
      for {
        aOrB <- effectOfPure(divide100By(n))
        c <- effectOf(aOrB.map(b => doSomethingBad(b)))
      } yield c
    )(MyError.nonFatalThrowable)

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](-1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>


## CanCatch.catchNonFatalEitherT
`CanCatch[F].catchNonFatalEitherT[A, B]` lets you catch `NonFatal` `Throwable` from `EitherT[F, A, B]`
 and returns `EitherT[F, A, B]`.

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
import cats.data.EitherT
import cats.effect._

import effectie.cats._

val fa = CanCatch[IO].catchNonFatalEitherT(
    EitherT(IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
  )(identity)

fa.value.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats.data.EitherT
import effectie.cats._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = CanCatch[Future].catchNonFatalEitherT(
    EitherT(Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
  )(identity)

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa.value, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import cats._
import cats.data.EitherT

import effectie.cats._

val fa = CanCatch[Id].catchNonFatalEitherT(
    EitherT((throw new RuntimeException("Something's wrong!")): Id[Either[Throwable, Int]])
  )(identity)

fa.value
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
import cats.data.EitherT
import cats.effect._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[IO](1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.EitherTSupport._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError
    
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[Id](1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
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
import cats.data.EitherT
import cats.implicits._
import cats.effect._

import effectie.cats._
import effectie.cats.EitherTSupport._
import effectie.Effectful._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[IO](-1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.EitherTSupport._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError
    
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](-1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[Id](-1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>


## Catching
`Catching.catchNonFatal` provides a convenient way to use `CanCatch` to catch `NonFatal` `Throwable` in the `F[A]`
 and turned it into `F[Either[Throwable, A]]`. Just like `CanCatch`, it takes a function from `Throwable` 
 to your own error type, yet it can handle only `NonFatal` ones as already mentioned.
 
## Catching.catchNonFatal
`catchNonFatal` lets you catch `NonFatal` `Throwable` from `F[B]`
 and returns `F[Either[A, B]]`.

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

import effectie.cats.Catching._

val fa = catchNonFatal(
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

import effectie.cats.Catching._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = catchNonFatal(
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

import effectie.cats.Catching._

catchNonFatal[Id](
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.concurrent.ExecutorServiceOps

object MyApp {

  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  }

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)

  def main(arg: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2


def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-101)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._


import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  }

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    catchNonFatal(
      for {
        a <- effectOfPure(n + 100)
        b <- effectOf(doSomethingBad(a))
      } yield b
    )(MyError.nonFatalThrowable)

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    try {
      val fa = doSomething[Future](-101)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)
}

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-101)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>

## Catching.catchNonFatalEither
`Catching.catchNonFatalEither` provides a convenient way to use `CanCatch` 
to catch `NonFatal` `Throwable` from `F[Either[A, B]]` and returns `F[Either[A, B]]`.

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

import effectie.cats.Catching._

val fa = catchNonFatalEither(
    IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
  )(identity)

fa.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.cats.Catching._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = catchNonFatalEither(
    Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
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

import effectie.cats.Catching._

catchNonFatalEither[Id](
    (throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError

    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]
  
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    catchNonFatalEither(
      for {
        aOrB <- effectOfPure(divide100By(n))
        c <- effectOf(aOrB.map(b => doSomethingBad(b)))
      } yield c
    )(MyError.nonFatalThrowable)

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    try {
      val fa = doSomething[Future](1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError
    
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    catchNonFatalEither(
      for {
        aOrB <- effectOfPure(divide100By(n))
        c <- effectOf(aOrB.map(b => doSomethingBad(b)))
      } yield c
    )(MyError.nonFatalThrowable)

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](-1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import effectie.cats.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>


## Catching.catchNonFatalEitherT
`Catching.catchNonFatalEitherT` provides a convenient way to use `CanCatch`
 to catch `NonFatal` `Throwable` from `EitherT[F, A, B]` and returns `EitherT[F, A, B]`.

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
import cats.data.EitherT
import cats.effect._

import effectie.cats.Catching._

val fa = catchNonFatalEitherT[IO](
    EitherT(IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
  )(identity)

fa.value.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats.data.EitherT

import effectie.cats.Catching._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = catchNonFatalEitherT[Future](
    EitherT(Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
  )(identity)

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa.value, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import cats._
import cats.data.EitherT

import effectie.cats.Catching._

val fa = catchNonFatalEitherT[Id](
    EitherT((throw new RuntimeException("Something's wrong!")): Id[Either[Throwable, Int]])
  )(identity)

fa.value
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
import cats.data.EitherT
import cats.effect._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[IO](1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.cats.EitherTSupport._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError
    
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[Id](1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
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
import cats.data.EitherT
import cats.implicits._
import cats.effect._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[IO](-1)
val result = fa.unsafeRunSync()
result match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.cats.EitherTSupport._
import effectie.concurrent.ExecutorServiceOps

object MyApp {
  sealed trait MyError
  object MyError {
    final case class NonFatalThrowable(throwable: Throwable) extends MyError
    case object DivideByZero extends MyError
    
    def nonFatalThrowable(throwable: Throwable): MyError
      = NonFatalThrowable(throwable)
  
    def divideByZero: MyError = DivideByZero
  }

  def divide100By(n: Int): Either[MyError, Int] =
    if (n === 0)
      MyError.divideByZero.asLeft[Int]
    else
      (100 / n).asRight[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      val fa = doSomething[Future](-1)
      println(fa)
      val result = Await.result(fa, 1.second)
      println(result)
      result match {
        case Right(b) =>
          println(s"Result is $b")
        case Left(a) =>
          println(s"Result: Failed with $a")
      }
    } finally {
      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)
    }
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
import cats.data.EitherT
import cats.implicits._

import effectie.cats._
import effectie.Effectful._
import effectie.cats.Catching._
import effectie.cats.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): Either[MyError, Int] =
  if (n === 0)
    MyError.divideByZero.asLeft[Int]
  else
    (100 / n).asRight[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).value

val fa = doSomething[Id](-1)
fa match {
  case Right(b) =>
    println(s"Result is $b")
  case Left(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>


