# maven2sbt

[![Build Status](https://github.com/Kevin-Lee/maven2sbt/workflows/Build%20All/badge.svg)](https://github.com/Kevin-Lee/maven2sbt/actions?workflow=Build+All)
[![Release Status](https://github.com/Kevin-Lee/maven2sbt/workflows/Release/badge.svg)](https://github.com/Kevin-Lee/maven2sbt/actions?workflow=Release)

[![Download](https://api.bintray.com/packages/kevinlee/maven/maven2sbt-core/images/download.svg)](https://bintray.com/kevinlee/maven/maven2sbt-core/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kevinlee/maven2sbt-core_2.13/badge.svg)](https://search.maven.org/artifact/io.kevinlee/maven2sbt-core_2.13)
[![Latest version](https://index.scala-lang.org/kevin-lee/maven2sbt/maven2sbt-core/latest.svg)](https://index.scala-lang.org/kevin-lee/maven2sbt/maven2sbt-core)

A tool to convert Maven `pom.xml` into sbt `build.sbt`


# Standalone CLI App

It requires Java 8 or higher. So JRE should be installed and available to run `maven2sbt-cli`.

## Debian / Ubuntu Linux
If you use Debian or Unbuntu Linux you can download [maven2sbt-cli_0.4.0_all.deb](https://github.com/Kevin-Lee/maven2sbt/releases/download/v0.4.0/maven2sbt-cli_0.4.0_all.deb) and install it.
```shell
$ dpkg -i maven2sbt-cli_0.4.0_all.deb 
```
`maven2sbt` should be available.
e.g.)
```shell
$ which maven2sbt
/usr/bin/maven2sbt
```


## Linux / macOS
### Use `curl`
```shell
sh -c "$(curl -fsSL https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install.sh)" 
```
***

### Or use `wget`
```shell
sh -c "$(wget -O- https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install.sh)" 
```
***

### Or do it manually (not recommended)
  
Download [maven2sbt-cli-0.4.0.zip](https://github.com/Kevin-Lee/maven2sbt/releases/download/v0.4.0/maven2sbt-cli-0.4.0.zip) or [maven2sbt-cli-0.4.0.tgz](https://github.com/Kevin-Lee/maven2sbt/releases/download/v0.4.0/maven2sbt-cli-0.4.0.tgz) and unzip it.
  
Add an alias for convenience to `~/.zshrc` or `~/.bashrc` or `~/.bash_profile` or the run commands file for your shell. 
```shell
alias maven2sbt='/path/to/maven2sbt-cli-0.4.0/bin/maven2sbt'
```


## Windows

Download and unzip the `maven2sbt-cli-0.4.0.zip` or `maven2sbt-cli-0.4.0.tgz` just like Linux or macOS.

You can run `maven2sbt-cli-0.4.0/bin/maven2sbt.bat` file but it hasn't been tested.


# How to Use

Now you can run it like

```shell
$ maven2sbt --help 

Usage:
  Maven2Sbt (file ARGS... | print ARGS...) [-v|--version VERSION] [-h|--help HELP]

A tool to convert Maven pom.xml into sbt build.sbt

Available options:
  -v|--version VERSION    Prints the application version.
  -h|--help HELP          Prints the synopsis and a list of options and arguments.


Available commands:
  file                    Convert pom.xml to sbt config and save in the file
  print                   Convert pom.xml to sbt config and print it out

```

## Save as sbt Config File
```shell
$ maven2sbt file --help

Usage:
  Maven2Sbt file -s|--scala-version <version> [-o|--out <file>] [--overwrite]
                 <pom-path> [-h|--help HELP]

Convert pom.xml to sbt config and save in the file

Available options:
  --overwrite             Overwrite if the output file already exists.
  -s|--scala-version <version> Scala version
  -o|--out <file>         output sbt config file (default: build.sbt)
  -h|--help HELP          Prints the synopsis and a list of options and arguments.

Positional arguments:
  <pom-path>              Path to the pom file.

```

e.g.)
```shell
$ maven2sbt file --scala-version 2.13.1 pom.xml
# or
$ maven2sbt file -s 2.13.1 pom.xml
```
It will generate `build.sbt`.

Save sbt config in a different file.
```shell
maven2sbt file --scala-version 2.13.1 --out something-else.sbt pom.xml
# or
maven2sbt file -s 2.13.1 -o something-else.sbt pom.xml
```
It will generate `something-else.sbt`.

I may faile if the output file already eixsts. If you want to overwrite, use the `--overwrite` option.

```shell
# build.sbt already exists and want to overwrite
maven2sbt file --scala-version 2.13.1 --overwrite pom.xml
# or
maven2sbt file -s 2.13.1 --overwrite pom.xml

# something-else.sbt already exists and want to overwrite
maven2sbt file --scala-version 2.13.1 --out something-else.sbt --overwrite pom.xml
# or
maven2sbt file -s 2.13.1 -o something-else.sbt --overwrite pom.xml
```

## Print Out
```shell
$ maven2sbt print --help

Usage:
  Maven2Sbt print -s|--scala-version <version> <pom-path> [-h|--help HELP]

Convert pom.xml to sbt config and print it out

Available options:
  -s|--scala-version <version> Scala version
  -h|--help HELP          Prints the synopsis and a list of options and arguments.

Positional arguments:
  <pom-path>              Path to the pom file.

```
e.g.)
```shell
$ maven2sbt print --scala-version 2.13.1 pom.xml
# or
$ maven2sbt print -s 2.13.1 pom.xml
```
It will print out the content of `build.sbt` generated from the given `pom.xml`


# Use As Library

`maven2sbt-core` supports Scala 2.11, 2.12 and 2.13.


## Get maven2sbt-core lib

Add it to `build.sbt`.
```sbt
libraryDependencies += "io.kevinlee" %% "maven2sbt-core" % "0.4.0"
```


## How to Use

### Get immediate result

```scala
import java.io.File

import maven2sbt.core.Maven2Sbt
import maven2sbt.core.ScalaVersion

import cats._

Maven2Sbt[Id].buildSbtFromPomFile(ScalaVersion("2.13.1"), new File("/path/to/pom.xml"))
// Id[Either[Maven2SbtError, String]]

// or

Maven2Sbt[Id].buildSbtFromInputStream(ScalaVersion("2.13.1"), inputStream)
// Id[Either[Maven2SbtError, String]]
```


### With Cats Effect

```scala
import java.io.File

import maven2sbt.core.Maven2Sbt
import maven2sbt.core.ScalaVersion

import cats.effect._

Maven2Sbt[IO].buildSbtFromPomFile(ScalaVersion("2.13.1"), new File("/path/to/pom.xml"))
// IO[Either[Maven2SbtError, String]]

// or

Maven2Sbt[IO].buildSbtFromInputStream(ScalaVersion("2.13.1"), inputStream)
// IO[Either[Maven2SbtError, String]]
```
