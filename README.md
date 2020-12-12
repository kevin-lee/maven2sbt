# maven2sbt

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2FKevin-Lee%2Fmaven2sbt)](https://hits.seeyoufarm.com)
[![Build Status](https://github.com/Kevin-Lee/maven2sbt/workflows/Build%20All/badge.svg)](https://github.com/Kevin-Lee/maven2sbt/actions?workflow=Build+All)
[![Release Status](https://github.com/Kevin-Lee/maven2sbt/workflows/Release/badge.svg)](https://github.com/Kevin-Lee/maven2sbt/actions?workflow=Release)

[![Download](https://api.bintray.com/packages/kevinlee/maven/maven2sbt-core/images/download.svg)](https://bintray.com/kevinlee/maven/maven2sbt-core/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/maven2sbt-core_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/maven2sbt-core_2.13)
[![Latest version](https://index.scala-lang.org/kevin-lee/maven2sbt/maven2sbt-core/latest.svg)](https://index.scala-lang.org/kevin-lee/maven2sbt/maven2sbt-core)

A tool to convert Maven `pom.xml` into sbt `build.sbt`
# Motivation of the project
## Why maven2sbt is better than sbt-pom-reader
### maven2sbt is more "reliable" than sbt-pom-reader
Even if maven2sbt doesn't work correctly when it compiles some part of pom.xml, user still can manually fix the output build.sbt  in order to get a runnable build.sbt. He won't never be blocked because a bug in maven2sbt. So even if maven2sbt isn't mature, contains bugs, it is still usable.
### sbt-pom-reader is contradictory
A developer want want use sbt tool on maven project, because he doesn't know/want maven pom.xml langage and maven cli tool.
With sbt-pom-reader, it solves only the problem of "doesn't know/want maven cli tool". If the user want modify/read the builder configuration file, he still need use pom.xml. It is contradictory with the reason that why he want use sbt on a maven project.
With maven2sbt, he doesn't have this problem.
### maven2sbt is more reusable than sbt-pom-reader
If it exists a compiler from sbt builder configuration to mill builder configuration: `sbt2mill`.
It is possible to create a new compiler maven builder configuration to mill builder configuration
```def maven2mill = maven2sbt compose sbt2mill```

# Please visit [https://maven2sbt.kevinly.dev](https://maven2sbt.kevinly.dev)
