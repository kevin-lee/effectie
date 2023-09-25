---
sidebar_position: 1
id: can-catch
title: "CanCatch"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## CanCatch
`CanCatch` lets you catch `NonFatal` `Throwable` in the `F[A]`
 and turned it into `F[Either[Throwable, A]]`. It takes a function from `Throwable` 
 to your own error type, yet it can handle only `NonFatal` ones as already mentioned.

`CanCatch` looks like this.
 
```scala
trait CanCatch[F[_]] {
  def catchNonFatal[A, B](fb: => F[B])(f: PartialFunction[Throwable, A]): F[Either[A, B]]

  def catchNonFatalEither[A, AA >: A, B](fab: => F[Either[A, B]])(
    f: PartialFunction[Throwable, AA]
  ): F[Either[AA, B]]
}
```

In practice, you don't need to use it directly because `Fx` is already `CanCatch` as well.

## CanCatch.catchNonFatal
<Tabs
  groupId="can-catch"
  defaultValue="fx"
  values={[
    {label: 'Fx', value: 'fx'},
    {label: 'CanCatch', value: 'cancatch'},
  ]}>
  <TabItem value="fx">

```scala
val fa: F[A] = ...
Fx[F].catchNonFatal(fa) {
  case SomeException(message) =>
    SomeError(message)
} // F[Either[SomeError, A]
```

  </TabItem>
  
  <TabItem value="cancatch">

```scala
val fa: F[A] = ...
CanCatch[F].catchNonFatal(fa) {
  case SomeException(message) =>
    SomeError(message)
} // F[Either[SomeError, A]
```

  </TabItem>
</Tabs>


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

```scala mdoc:reset-object:height=4
import cats.effect._

import effectie.core._
import effectie.instances.ce2.fx._

final case class MyException(cause: Throwable) extends RuntimeException

val fa = CanCatch[IO].catchNonFatal(
    IO(throw new RuntimeException("Something's wrong!"))
  ) {
    case ex =>
      MyException(ex)
  }

fa.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.core._
import effectie.instances.future.fx._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

final case class MyException(cause: Throwable) extends RuntimeException

val fa = CanCatch[Future].catchNonFatal(
    Future(throw new RuntimeException("Something's wrong!"))
  ) {
    case ex =>
      MyException(ex)
  }

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object:height=4
import cats._

import effectie.core._
import effectie.instances.id.fx._

final case class MyException(cause: Throwable) extends RuntimeException

CanCatch[Id].catchNonFatal(
    throw new RuntimeException("Something's wrong!")
  ) {
    case ex =>
      MyException(ex)
  }
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- pureOf(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }

import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- pureOf(n + 100)
        b <- effectOf(doSomethingBad(a))
      } yield b
    ) {
      case ex => MyError.nonFatalThrowable(ex)
    }

  def main(arg: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    import effectie.instances.future.fx._
    
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- pureOf(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }
  
import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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


def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- pureOf(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }
  
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._


import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatal(
      for {
        a <- pureOf(n + 100)
        b <- effectOf(doSomethingBad(a))
      } yield b
    ) {
      case ex => MyError.nonFatalThrowable(ex)
    }

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    import effectie.instances.future.fx._
    
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatal(
    for {
      a <- pureOf(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }
  
import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats.effect._

import effectie.core._
import effectie.instances.ce2.fx._

final case class MyException(cause: Throwable) extends RuntimeException

val fa = CanCatch[IO].catchNonFatalEither(
    IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
  ) {
    case ex => MyException(ex)
  }

fa.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.core._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.future.fx._

val fa = CanCatch[Future].catchNonFatalEither(
    Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
  ) {
    case ex => MyException(ex)
  }

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object:height=4
import cats._

import effectie.core._
import effectie.instances.id.fx._

final case class MyException(cause: Throwable) extends RuntimeException

CanCatch[Id].catchNonFatalEither(
    (throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]
  ){
    case ex => MyException(ex)
  }
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- pureOf(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }
  
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEither(
      for {
        aOrB <- pureOf(divide100By(n))
        c <- effectOf(aOrB.map(b => doSomethingBad(b)))
      } yield c
    ) {
      case ex => MyError.nonFatalThrowable(ex)
    }

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    import effectie.instances.future.fx._
    
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- pureOf(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }
  
import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- pureOf(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }

import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEither(
      for {
        aOrB <- pureOf(divide100By(n))
        c <- effectOf(aOrB.map(b => doSomethingBad(b)))
      } yield c
    ) {
      case ex => MyError.nonFatalThrowable(ex)
    }

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    import effectie.instances.future.fx._
    
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- pureOf(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }
  
import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats.data.EitherT
import cats.effect._

import effectie.core._
import effectie.syntax.all._
import effectie.instances.ce2.fx._

final case class MyException(cause: Throwable) extends RuntimeException

val fa = CanCatch[IO].catchNonFatalEitherT(
    EitherT(IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
  ) {
    case ex => MyException(ex)
  }

fa.value.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats.data.EitherT
import effectie.core._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.syntax.all._
import effectie.instances.future.fx._

val fa = CanCatch[Future].catchNonFatalEitherT(
    EitherT(Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
  ) {
    case ex => MyException(ex)
  }

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa.value, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object:height=4
import cats._
import cats.data.EitherT

import effectie.core._
import effectie.syntax.all._
import effectie.instances.id.fx._

final case class MyException(cause: Throwable) extends RuntimeException

val fa = CanCatch[Id].catchNonFatalEitherT(
    EitherT((throw new RuntimeException("Something's wrong!")): Id[Either[Throwable, Int]])
  ) {
    case ex => MyException(ex)
  }

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- pureOf(divide100By(n)).eitherT
      c <- doSomethingBad(b).rightTF[F, MyError]
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }.value
  
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEitherT(
      for {
        b <- pureOf(divide100By(n)).eitherT
        c <- doSomethingBad(b).rightTF[F, MyError]
      } yield c
    ) {
      case ex => MyError.nonFatalThrowable(ex)
    }.value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    import effectie.instances.future.fx._
    
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- pureOf(divide100By(n)).eitherT
      c <- doSomethingBad(b).rightTF[F, MyError]
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }.value

import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- pureOf(divide100By(n)).eitherT
      c <- doSomethingBad(b).rightTF[F, MyError]
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }.value

import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

import extras.cats.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    CanCatch[F].catchNonFatalEitherT(
      for {
        b <- pureOf(divide100By(n)).eitherT
        c <- doSomethingBad(b).rightTF[F, MyError]
      } yield c
    ) {
      case ex => MyError.nonFatalThrowable(ex)
    }.value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    import effectie.instances.future.fx._
    
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- pureOf(divide100By(n)).eitherT
      c <- doSomethingBad(b).rightTF[F, MyError]
    } yield c
  ) {
    case ex => MyError.nonFatalThrowable(ex)
  }.value
  
import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats.effect._

import effectie.syntax.all._

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.ce2.fx._

val fa = 
  IO(throw new RuntimeException("Something's wrong!"))
    .catchNonFatal {
      case ex => MyException(ex)
    }

fa.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.syntax.all._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.future.fx._

val fa = 
    Future(throw new RuntimeException("Something's wrong!"))
      .catchNonFatal {
        case ex => MyException(ex)
      }

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object:height=4
//import cats._
//
//import effectie.syntax.all._
//import effectie.instances.id.fx._
//
//final case class MyException(cause: Throwable) extends RuntimeException
//
//catchNonFatal[Id](
//    throw new RuntimeException("Something's wrong!")
//  ) {
//    case ex => MyException(ex)
//  }
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    a <- pureOf(n + 100)
    b <- effectOf(doSomethingBad(a))
  } yield b)
    .catchNonFatal {
      case ex => MyError.nonFatalThrowable(ex)
    }
    
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    (for {
      a <- pureOf(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b)
      .catchNonFatal {
        case ex => MyError.nonFatalThrowable(ex)
      }

  def main(arg: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      import effectie.instances.future.fx._
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    a <- pureOf(n + 100)
    b <- effectOf(doSomethingBad(a))
  } yield b)
    .catchNonFatal {
      case ex => MyError.nonFatalThrowable(ex)
    }

import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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


def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    a <- pureOf(n + 100)
    b <- effectOf(doSomethingBad(a))
  } yield b)
    .catchNonFatal {
      case ex => MyError.nonFatalThrowable(ex)
    }

import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._


import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    (for {
      a <- pureOf(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b)
      .catchNonFatal {
        case ex => MyError.nonFatalThrowable(ex)
      }

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    try {
      import effectie.instances.future.fx._
      
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    a <- pureOf(n + 100)
    b <- effectOf(doSomethingBad(a))
  } yield b)
    .catchNonFatal {
      case ex => MyError.nonFatalThrowable(ex)
    }

import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats.effect._

import effectie.syntax.all._

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.ce2.fx._

val fa = 
    IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
      .catchNonFatalEither {
        case ex => MyException(ex)
      }

fa.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.syntax.all._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.future.fx._

val fa = 
  Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int])
    .catchNonFatalEither {
      case ex => MyException(ex)
    }

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object:height=4
//import cats._
//
//import effectie.syntax.all._
//
//catchNonFatalEither[Id](
//    (throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]
//  )(identity)
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    aOrB <- pureOf(divide100By(n))
    c <- effectOf(aOrB.map(b => doSomethingBad(b)))
  } yield c)
    .catchNonFatalEither {
      case ex => MyError.nonFatalThrowable(ex)
    }
    
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    (for {
      aOrB <- pureOf(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c)
      .catchNonFatalEither {
        case ex => MyError.nonFatalThrowable(ex)
      }

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

    try {
      import effectie.instances.future.fx._
      
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    aOrB <- pureOf(divide100By(n))
    c <- effectOf(aOrB.map(b => doSomethingBad(b)))
  } yield c)
    .catchNonFatalEither {
      case ex => MyError.nonFatalThrowable(ex)
    }

import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    aOrB <- pureOf(divide100By(n))
    c <- effectOf(aOrB.map(b => doSomethingBad(b)))
  } yield c)
    .catchNonFatalEither {
      case ex => MyError.nonFatalThrowable(ex)
    }

import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    (for {
      aOrB <- pureOf(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c)
      .catchNonFatalEither {
        case ex => MyError.nonFatalThrowable(ex)
      }

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      import effectie.instances.future.fx._
      
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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    aOrB <- pureOf(divide100By(n))
    c <- effectOf(aOrB.map(b => doSomethingBad(b)))
  } yield c)
    .catchNonFatalEither {
      case ex => MyError.nonFatalThrowable(ex)
    }

import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats.data.EitherT
import cats.effect._

import effectie.syntax.all._

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.ce2.fx._

val fa = 
  EitherT(IO((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
    .catchNonFatalEitherT {
      case ex => MyException(ex)
    }

fa.value.unsafeRunSync()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats.data.EitherT

import effectie.syntax.all._

implicit val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.future.fx._

val fa = 
  EitherT(Future((throw new RuntimeException("Something's wrong!")): Either[Throwable, Int]))
    .catchNonFatalEitherT {
      case ex => MyException(ex)
    }

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa.value, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object:height=4
import cats._
import cats.data.EitherT

import effectie.syntax.all._

final case class MyException(cause: Throwable) extends RuntimeException

import effectie.instances.id.fx._

val fa =
  EitherT((throw new RuntimeException("Something's wrong!")): Id[Either[Throwable, Int]])
    .catchNonFatalEitherT {
      case ex => MyException(ex)
    }

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

```scala mdoc:reset-object:height=4
import cats._
import cats.syntax.all._
import cats.data.EitherT
import cats.effect._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    b <- EitherT(pureOf(divide100By(n)))
    c <- doSomethingBad(b).rightTF[F, MyError]
  } yield c)
    .catchNonFatalEitherT {
      case ex => MyError.nonFatalThrowable(ex)
    }.value
    
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.data.EitherT
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    (for {
      b <- EitherT(pureOf(divide100By(n)))
      c <- doSomethingBad(b).rightTF[F, MyError]
    } yield c)
      .catchNonFatalEitherT {
        case ex => MyError.nonFatalThrowable(ex)
      }.value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      import effectie.instances.future.fx._
      
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

```scala mdoc:reset-object:height=4
import cats._
import cats.data.EitherT
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    b <- EitherT(pureOf(divide100By(n)))
    c <- doSomethingBad(b).rightTF[F, MyError]
  } yield c)
    .catchNonFatalEitherT {
      case ex => MyError.nonFatalThrowable(ex)
    }.value

import effectie.instances.id.fx._

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

```scala mdoc:reset-object:height=4
import cats._
import cats.data.EitherT
import cats.syntax.all._
import cats.effect._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    b <- EitherT(pureOf(divide100By(n)))
    c <- doSomethingBad(b).rightTF[F, MyError]
  } yield c)
    .catchNonFatalEitherT {
      case ex => MyError.nonFatalThrowable(ex)
    }.value
    
import effectie.instances.ce2.fx._

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

```scala mdoc:reset-object:height=4
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import cats._
import cats.data.EitherT
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._
import extras.concurrent.ExecutorServiceOps

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

  def doSomething[F[_]: Fx: CanCatch: Monad](
    n: Int
  ): F[Either[MyError, Int]] =
    (for {
      b <- EitherT(pureOf(divide100By(n)))
      c <- doSomethingBad(b).rightTF[F, MyError]
    } yield c)
      .catchNonFatalEitherT {
        case ex => MyError.nonFatalThrowable(ex)
      }.value

  def main(args: Array[String]): Unit = {
    val executorService: ExecutorService =
      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)
    
    try {
      import effectie.instances.future.fx._
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

```scala mdoc:reset-object:height=4
import cats._
import cats.data.EitherT
import cats.syntax.all._

import effectie.core._
import effectie.syntax.all._
import extras.cats.syntax.all._

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

def doSomething[F[_]: Fx: CanCatch: Monad](
  n: Int
): F[Either[MyError, Int]] =
  (for {
    b <- EitherT(pureOf(divide100By(n)))
    c <- doSomethingBad(b).rightTF[F, MyError]
  } yield c)
    .catchNonFatalEitherT {
      case ex => MyError.nonFatalThrowable(ex)
    }.value
    
import effectie.instances.id.fx._

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


