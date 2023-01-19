---
sidebar_position: 1
id: getting-started
title: Getting Started
slug: "/"
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## ![Effectie Logo](/img/effectie-logo-96x96.png) Effectie

[![Build Status](https://github.com/Kevin-Lee/effectie/workflows/Build-All/badge.svg)](https://github.com/Kevin-Lee/effectie/actions?workflow=Build-All)
[![Release Status](https://github.com/Kevin-Lee/effectie/workflows/Release/badge.svg)](https://github.com/Kevin-Lee/effectie/actions?workflow=Release)
[![Latest version](https://index.scala-lang.org/kevin-lee/effectie/latest.svg)](https://index.scala-lang.org/kevin-lee/effectie)


|               Project | Maven Central                                                                                                                                                                                   |
|----------------------:|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| effectie-cats-effect3 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-cats-effect3_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/effectie-cats-effect3_2.13) |
| effectie-cats-effect2 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-cats-effect2_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/effectie-cats-effect2_2.13) |
|       effectie-monix3 | [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-monix3_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/effectie-monix3_2.13)             |

* Supported Scala Versions: @SUPPORTED_SCALA_VERSIONS@

A set of type-classes and utils for functional effect libraries (i.e.  Cats Effect, Monix and Scalaz's Effect).

Why Effectie? Please read ["Why?"](#why) section.

## Getting Started
### For Cats Effect

In `build.sbt`,

<Tabs
groupId="cats-effect"
defaultValue="cats-effect"
values={[
{label: 'Cats Effect 3', value: 'cats-effect3'},
{label: 'Cats Effect 2', value: 'cats-effect'},
]}>
<TabItem value="cats-effect3">

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect3" % "@VERSION@"
```

  </TabItem>

  <TabItem value="cats-effect">

```scala
libraryDependencies += "io.kevinlee" %% "effectie-cats-effect2" % "@VERSION@"
```

  </TabItem>
</Tabs>

For more details, check out [Effectie for Cats Effect](cats-effect2/cats-effect2.md).


### For Monix

In `build.sbt`,

```scala
libraryDependencies += "io.kevinlee" %% "effectie-monix" % "@VERSION@"
```

For more details, check out [Effectie for Monix](monix3/monix3.md).



## Why?
Tagless final gives us power to defer the decision of the implementations of contexts we're binding and functional effect libraries like Cats Effect and Monix give us referential transparency (and more). There might be an issue though with writing implementation for the abstraction which is supposed to support not only effect libraries like Cats Effect but also `Future`. You may end up writing exactly the same code with only exception of how you construct effect data type (e.g. `IO` vs `Future`). 

Let's check out some code examples.

e.g.) Cats Effect
```scala mdoc:reset-object
import cats.effect._

// or cats.Monad[IO].pure(println("a"))
val io = IO.pure(println("a"))
// It is not referentially transparent so immediately evaluates println("a") 

io.unsafeRunSync()
io.unsafeRunSync()
```

e.g.) Future

WIP...

Check out
* [Effectie for Cats Effect 2](cats-effect2/cats-effect2.md)
* [Effectie for Monix 3](monix3/monix3.md)
