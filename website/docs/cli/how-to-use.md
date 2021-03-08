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
  Maven2Sbt file -s|--scala-version <version> [-b|--scala-binary-version-name
                 <scala-binary-version-name>] [--props-name <props-name>]
                 [--libs-name <libs-name>] [-o|--out <file>] [--overwrite]
                 <pom-path> [-h|--help HELP]

Convert pom.xml to sbt config and save in the file

Available options:
  --overwrite             Overwrite if the output file already exists.
  -s|--scala-version <version> Scala version
  -b|--scala-binary-version-name <scala-binary-version-name> The name of Scala
                          binary version property. This is useful to figure out
                          if it is a Scala library or Java library
                          e.g.)
                          -b scala.binary
                          # or
                          --scala-binary-version-name scala.binary
                          ---
                          <properties>
                            <scala.binary>2.13</scala.binary>
                          </properties>
                          <dependencies>
                            <dependency>
                              <groupId>io.kevinlee</groupId>
                              <artifactId>myLib1_${scala.binary}</artifactId>
                              <version>0.1.0</version>
                            </dependency>
                            <dependency>
                              <groupId>io.kevinlee</groupId>
                              <artifactId>myLib2</artifactId>
                              <version>0.2.0</version>
                            </dependency>
                          </dependencies>
                          ---
                          results in
                          "io.kevinlee" %% "myLib1" % "0.1.0"
                          "io.kevinlee" % "myLib2" % "0.1.0"
                          ---
  --props-name <props-name> properties object name (e.g. 'props' in `lazy val
                          props = new {}`) (default: props)
  --libs-name <libs-name> The name of the object containing all the libraries to
                          re-use (e.g. 'libs' in `lazy val libs = new {}`)
                          (default: libs)
  -o|--out <file>         output sbt config file (default: build.sbt)
  -h|--help HELP          Prints the synopsis and a list of options and arguments.

Positional arguments:
  <pom-path>              Path to the pom file.

```

e.g.)
```shell
$ maven2sbt file --scala-version 2.13.4 --scala-binary-version-name scalaBinaryVersion pom.xml
# or
$ maven2sbt file -s 2.13.4 -b scalaBinaryVersion pom.xml
```
or
```shell
$ maven2sbt file --scala-version 2.13.4 --props-name myProps pom.xml
# or
$ maven2sbt file -s 2.13.4 --props-name myProps pom.xml
```
It will generate `build.sbt`.

Save sbt config in a different file.
```shell
maven2sbt file --scala-version 2.13.4 --out something-else.sbt pom.xml
# or
maven2sbt file -s 2.13.4 -o something-else.sbt pom.xml
```
It will generate `something-else.sbt`.

I may faile if the output file already eixsts. If you want to overwrite, use the `--overwrite` option.

```shell
# build.sbt already exists and want to overwrite
maven2sbt file --scala-version 2.13.4 --overwrite pom.xml
# or
maven2sbt file -s 2.13.4 --overwrite pom.xml

# something-else.sbt already exists and want to overwrite
maven2sbt file --scala-version 2.13.4 --out something-else.sbt --overwrite pom.xml
# or
maven2sbt file -s 2.13.4 -o something-else.sbt --overwrite pom.xml
```

## Print Out
```shell
$ maven2sbt print --help

Usage:
  Maven2Sbt print -s|--scala-version <version> [-b|--scala-binary-version-name
                  <scala-binary-version-name>] [--props-name <props-name>]
                  [--libs-name <libs-name>] <pom-path> [-h|--help HELP]

Convert pom.xml to sbt config and print it out

Available options:
  -s|--scala-version <version> Scala version
  -b|--scala-binary-version-name <scala-binary-version-name> The name of Scala
                          binary version property. This is useful to figure out
                          if it is a Scala library or Java library
                          e.g.)
                          -b scala.binary
                          # or
                          --scala-binary-version-name scala.binary
                          ---
                          <properties>
                            <scala.binary>2.13</scala.binary>
                          </properties>
                          <dependencies>
                            <dependency>
                              <groupId>io.kevinlee</groupId>
                              <artifactId>myLib1_${scala.binary}</artifactId>
                              <version>0.1.0</version>
                            </dependency>
                            <dependency>
                              <groupId>io.kevinlee</groupId>
                              <artifactId>myLib2</artifactId>
                              <version>0.2.0</version>
                            </dependency>
                          </dependencies>
                          ---
                          results in
                          "io.kevinlee" %% "myLib1" % "0.1.0"
                          "io.kevinlee" % "myLib2" % "0.1.0"
                          ---
  --props-name <props-name> properties object name (e.g. 'props' in `lazy val
                          props = new {}`) (default: props)
  --libs-name <libs-name> The name of the object containing all the libraries to
                          re-use (e.g. 'libs' in `lazy val libs = new {}`)
                          (default: libs)
  -h|--help HELP          Prints the synopsis and a list of options and arguments.

Positional arguments:
  <pom-path>              Path to the pom file.

```
e.g.)
```shell
$ maven2sbt print --scala-version 2.13.4 --scala-binary-version-name scalaBinaryVersion pom.xml
# or
$ maven2sbt print -s 2.13.4 -b scalaBinaryVersion pom.xml
```
or
```shell
$ maven2sbt print --scala-version 2.13.4 --props-name myProps pom.xml
# or
$ maven2sbt print -s 2.13.4 --props-name myProps pom.xml
```
It will print out the content of `build.sbt` generated from the given `pom.xml`

