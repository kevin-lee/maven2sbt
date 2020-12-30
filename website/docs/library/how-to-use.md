---
id: 'how-to-use'
title: 'Use maven2sbt as a Library'
sidebar_label: 'How to Use Library'
---

## How to Use

### Get immediate result

```scala
import java.io.File

import maven2sbt.core.Maven2Sbt
import maven2sbt.core.Props
import maven2sbt.core.ScalaVersion

import cats._

Maven2Sbt[Id].buildSbtFromPomFile(
  ScalaVersion("2.13.3"),
  Props.PropsName("props"),
  new File("/path/to/pom.xml")
)
// Id[Either[Maven2SbtError, BuildSbt]]

// or

Maven2Sbt[Id].buildSbtFromInputStream(
  ScalaVersion("2.13.3"),
  Props.PropsName("props"),
  inputStream
)
// Id[Either[Maven2SbtError, BuildSbt]]
```


### With Cats Effect

```scala
import java.io.File

import maven2sbt.core.Maven2Sbt
import maven2sbt.core.Props
import maven2sbt.core.ScalaVersion

import cats.effect._

Maven2Sbt[IO].buildSbtFromPomFile(
  ScalaVersion("2.13.1"),
  Props.PropsName("props"),
  new File("/path/to/pom.xml")
)
// IO[Either[Maven2SbtError, BuildSbt]]

// or

Maven2Sbt[IO].buildSbtFromInputStream(
  ScalaVersion("2.13.1"),
  Props.PropsName("props"),
  inputStream
)
// IO[Either[Maven2SbtError, BuildSbt]]
```
