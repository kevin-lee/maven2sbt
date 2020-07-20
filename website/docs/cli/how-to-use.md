---
id: 'how-to-use'
title: 'Use maven2sbt CLI'
sidebar_label: 'How to Use'
---

## How to Use CLI

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

