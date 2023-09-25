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

| Fx                 | Cats Effect                | Future                   | Try           |
|--------------------|----------------------------|--------------------------|---------------|
| `pureOf(a)`        | `Sync[F].pure(a)`          | `Future.successful(a)`   | `Success(a)`  |
| `effectOf(a)`      | `Sync[F].delay(a)`         | `Future(a)`              | `Try(a)`      |
| `pureOrError(a)`   | `Sync[F].catchNonFatal(a)` | `Future.fromTry(Try(a))` | `Try(a)`      |
| `unitOf[F]`        | `Sync[F].unit`             | `Future.unit`            | `Success(())` |
| `errorOf[F][A](e)` | `Sync[F].raiseError[A](e)` | `Future.failed(e)`       | `Failure(e)`  |

...More will be added...
