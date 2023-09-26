---
sidebar_position: 3
id: "fx-cheat-sheet"
title: "Fx Cheat sheet"
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

After importing `syntax`,

```scala
import effectie.syntax.all._
```

## Construct

| Fx                 | Cats Effect                | Future                   | Try           |
|--------------------|----------------------------|--------------------------|---------------|
| `pureOf(a)`        | `Sync[F].pure(a)`          | `Future.successful(a)`   | `Success(a)`  |
| `effectOf(a)`      | `Sync[F].delay(a)`         | `Future(a)`              | `Try(a)`      |
| `pureOrError(a)`   | `Sync[F].catchNonFatal(a)` | `Future.fromTry(Try(a))` | `Try(a)`      |
| `unitOf[F]`        | `Sync[F].unit`             | `Future.unit`            | `Success(())` |
| `errorOf[F][A](e)` | `Sync[F].raiseError[A](e)` | `Future.failed(e)`       | `Failure(e)`  |

:::note
`a: A` <br />
`e: Throwable`
:::

## Error Handling

| Fx                                              | Cats Effect                                             | Future                                   | Try                                   |
|-------------------------------------------------|---------------------------------------------------------|------------------------------------------|---------------------------------------|
| `fa.catchNonFatalThrowable`                     | `fa.attempt`                                            | `fa.transform {...}`                     | `fa match { ... }`                    |
| `fb.catchNonFatal { ... }`                      | `fa.attempt.flatMap { ... }`                            | `fa.transform {...}.flatMap`             | `fa match { ... }.flatMap`            |
| `fa.handleNonFatal(e => A)`                     | `fa.handleError(a => A)`                                | `fa.recoverWith(e => Future(A))`         | `fa.recoverWith(e => Try(A))`         |
| `fa.handleNonFatalWith(e => F[A])`              | `fa.handleNonFatalWith(a => F[A])`                      | `fa.recoverWith(e => Future[A])`         | `fa.recoverWith(e => Try(A))`         |
| `fa.recoverFromNonFatalWith { case e => F[A] }` | `fa.handleErrorWith { case e => F[A] or raise e }`      | `fa.recoverWith { case e => Future[A] }` | `fa.recoverWith { case e => Try(A) }` |
| `fa.recoverFromNonFatal { case e => A }`        | `fa.handleErrorWith { case e => A.pure[F] or raise e }` | `fa.recover { case e => A }`             | `fa.recover { case e => A }`          |

:::note
`a: A` <br />
`b: B` <br />
`fa: F[A]` <br />
`fb: F[B]` <br />
`e: Throwable`
:::

...More will be added...
