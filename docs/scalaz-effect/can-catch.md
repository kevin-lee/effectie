---
id: can-catch
title: "CanCatch - Scalaz"
---
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## CanCatch
`CanCatch` lets you catch `NonFatal` `Throwable` in the `F[A]`
 and turned it into `F[Either[Throwable, A]]`. It takes a function from `Throwable` 
 to your own error type, yet it can handle only `NonFatal` ones as already mentioned.
 
```scala
trait CanCatch[F[_]] {
  def catchNonFatal[A, B](fb: => F[B])(f: Throwable => A): F[A \/ B]

  def catchNonFatalEither[A, B](fab: => F[A \/ B])(f: Throwable => A): F[A \/ B]

  def catchNonFatalEitherT[A, B](fab: => EitherT[F, A, B])(f: Throwable => A): EitherT[F, A, B]
}
```

## CanCatch.catchNonFatal
`CanCatch[F].catchNonFatal[A, B]` lets you catch `NonFatal` `Throwable` from `F[B]`
 and returns `F[A \/ B]`.

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
import scalaz.effect._

import effectie.scalaz._

val fa = CanCatch[IO].catchNonFatal(
    IO(throw new RuntimeException("Something's wrong!"))
  )(identity)

fa.unsafePerformIO()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.scalaz._

val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
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
import scalaz._
import Scalaz._

import effectie.scalaz._

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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
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
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
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
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
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
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
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
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-101)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._


import scalaz._
import Scalaz._

import effectie.scalaz._
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
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
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
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-101)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>

## CanCatch.catchNonFatalEither
`CanCatch[F].catchNonFatalEither[A, B]` lets you catch `NonFatal` `Throwable` from `F[A \/ B]`
 and returns `F[A \/ B]`.

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
import scalaz._
import scalaz.effect._

import effectie.scalaz._

val fa = CanCatch[IO].catchNonFatalEither(
    IO((throw new RuntimeException("Something's wrong!")): Throwable \/ Int)
  )(identity)

fa.unsafePerformIO()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._

import effectie.scalaz._

val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = CanCatch[Future].catchNonFatalEither(
    Future((throw new RuntimeException("Something's wrong!")): Throwable \/ Int)
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
import scalaz._
import Scalaz._

import effectie.scalaz._

CanCatch[Id].catchNonFatalEither(
    (throw new RuntimeException("Something's wrong!")): Throwable \/ Int
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]
  
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
import scalaz._
import scalaz.effect._

import effectie.scalaz._

val fa = CanCatch[IO].catchNonFatalEitherT(
    EitherT(IO((throw new RuntimeException("Something's wrong!")): Throwable \/ Int))
  )(identity)

fa.run.unsafePerformIO()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import effectie.scalaz._

val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = CanCatch[Future].catchNonFatalEitherT(
    EitherT(Future((throw new RuntimeException("Something's wrong!")): Throwable \/ Int))
  )(identity)

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa.run, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import scalaz._
import Scalaz._

import effectie.scalaz._

val fa = CanCatch[Id].catchNonFatalEitherT(
    EitherT((throw new RuntimeException("Something's wrong!")): Id[Throwable \/ Int])
  )(identity)

fa.run
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[IO](1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.EitherTSupport._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
    CanCatch[F].catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).run

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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[Id](1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.scalaz.EitherTSupport._
import effectie.Effectful._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[IO](-1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.EitherTSupport._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
    CanCatch[F].catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).run

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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  CanCatch[F].catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[Id](-1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
 and returns `F[A \/ B]`.

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
import scalaz.effect._

import effectie.scalaz.Catching._

val fa = catchNonFatal(
    IO((throw new RuntimeException("Something's wrong!")): Int)
  )(identity)

fa.unsafePerformIO()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import effectie.scalaz.Catching._

val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
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
import scalaz._
import Scalaz._

import effectie.scalaz.Catching._

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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

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
): F[MyError \/ Int] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
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
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

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
): F[MyError \/ Int] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

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
): F[MyError \/ Int] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-101)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._


import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
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
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

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
): F[MyError \/ Int] =
  catchNonFatal(
    for {
      a <- effectOfPure(n + 100)
      b <- effectOf(doSomethingBad(a))
    } yield b
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-101)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(MyError.NonFatalThrowable(a)) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>

## Catching.catchNonFatalEither
`Catching.catchNonFatalEither` provides a convenient way to use `CanCatch` 
to catch `NonFatal` `Throwable` from `F[A \/ B]` and returns `F[A \/ B]`.

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
import scalaz._
import scalaz.effect._

import effectie.scalaz.Catching._

val fa = catchNonFatalEither(
    IO((throw new RuntimeException("Something's wrong!")): Throwable \/ Int)
  )(identity)

fa.unsafePerformIO()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._

import effectie.scalaz.Catching._

val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = catchNonFatalEither(
    Future((throw new RuntimeException("Something's wrong!")): Throwable \/ Int)
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
import scalaz._
import Scalaz._

import effectie.scalaz.Catching._

catchNonFatalEither[Id](
    (throw new RuntimeException("Something's wrong!")): Throwable \/ Int
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]
  
  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[IO](-1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEither(
    for {
      aOrB <- effectOfPure(divide100By(n))
      c <- effectOf(aOrB.map(b => doSomethingBad(b)))
    } yield c
  )(MyError.nonFatalThrowable)

val fa = doSomething[Id](-1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
import scalaz._
import scalaz.effect._

import effectie.scalaz.Catching._

val fa = catchNonFatalEitherT[IO](
    EitherT(IO((throw new RuntimeException("Something's wrong!")): Throwable \/ Int))
  )(identity)

fa.run.unsafePerformIO()
```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._

import effectie.scalaz.Catching._

val executorService: ExecutorService = Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors())
implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)

val fa = catchNonFatalEitherT[Future](
    EitherT(Future((throw new RuntimeException("Something's wrong!")): Throwable \/ Int))
  )(identity)

// Just for this example, you wouldn't want to do it in your production code
Await.result(fa.run, Duration.Inf)
```

  </TabItem>
  
  <TabItem value="id">

:::caution NOTE
Use of `Id` is not recommended as `Id` means having no `Effect`. Use it only for some special cases like testing.
:::

```scala mdoc:reset-object
import scalaz._
import Scalaz._

import effectie.scalaz.Catching._

val fa = catchNonFatalEitherT[Id](
    EitherT((throw new RuntimeException("Something's wrong!")): Id[Throwable \/ Int])
  )(identity)

fa.run
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[IO](1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>

  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
import effectie.scalaz.EitherTSupport._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
    catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).run

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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[Id](1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
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
import scalaz._
import Scalaz._
import scalaz.effect._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
if (n < 0)
  throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
else
  n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[IO](-1)
val result = fa.unsafePerformIO()
result match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}

```

  </TabItem>
  
  <TabItem value="future">

```scala mdoc:reset-object
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, Future, Await}
import scala.concurrent.duration._

import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
import effectie.scalaz.EitherTSupport._
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

  def divide100By(n: Int): MyError \/ Int =
    if (n === 0)
      MyError.divideByZero.left[Int]
    else
      (100 / n).right[MyError]

  def doSomethingBad(n: Int): Int =
    if (n < 0)
      throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
    else
      n * 2

  def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
    n: Int
  ): F[MyError \/ Int] =
    catchNonFatalEitherT(
      for {
        b <- EitherT(effectOfPure(divide100By(n)))
        c <- eitherTRight[MyError](doSomethingBad(b))
      } yield c
    )(MyError.nonFatalThrowable).run

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
        case \/-(b) =>
          println(s"Result is $b")
        case -\/(a) =>
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
import scalaz._
import Scalaz._

import effectie.scalaz._
import effectie.Effectful._
import effectie.scalaz.Catching._
import effectie.scalaz.EitherTSupport._

sealed trait MyError
object MyError {
  final case class NonFatalThrowable(throwable: Throwable) extends MyError
  case object DivideByZero extends MyError
  
  def nonFatalThrowable(throwable: Throwable): MyError
    = NonFatalThrowable(throwable)

  def divideByZero: MyError = DivideByZero
}

def divide100By(n: Int): MyError \/ Int =
  if (n === 0)
    MyError.divideByZero.left[Int]
  else
    (100 / n).right[MyError]

def doSomethingBad(n: Int): Int =
  if (n < 0)
    throw new IllegalArgumentException(s"n cannot be a negative number. [n: $n]")
  else
    n * 2

def doSomething[F[_]: EffectConstructor: CanCatch: Monad](
  n: Int
): F[MyError \/ Int] =
  catchNonFatalEitherT(
    for {
      b <- EitherT(effectOfPure(divide100By(n)))
      c <- eitherTRight[MyError](doSomethingBad(b))
    } yield c
  )(MyError.nonFatalThrowable).run

val fa = doSomething[Id](-1)
fa match {
  case \/-(b) =>
    println(s"Result is $b")
  case -\/(a) =>
    println(s"Result: Failed with $a")
}
```

  </TabItem>
</Tabs>


