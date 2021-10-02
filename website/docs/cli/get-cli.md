---
id: 'get-cli'
title: 'Get maven2sbt CLI'
sidebar_label: 'Get CLI'
---


## Standalone CLI App

It requires Java 11 or higher. So JRE should be installed and available to run `maven2sbt-cli`.

### macOS (Native)


#### Install GraalVM Native Image (Recommended)

Just run the following one in the terminal then it installs maven2sbt GraalVM Native Image which does not require JVM so much faster to run.

:::tip Recommended

```shell
bash -c "$(curl -fsSL https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install-graal-macos.sh)"
```
:::

### Debian / Ubuntu Linux

#### Install GraalVM Native Image (Recommended)

Just run the following one in the terminal then it installs maven2sbt GraalVM Native Image which does not require JVM so much faster to run.

:::tip Recommended

```shell
bash -c "$(curl -fsSL https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install-graal-ubuntu.sh)"
```

:::

### Linux / macOS (JVM)
#### Use `curl`
```shell
bash -c "$(curl -fsSL https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install.sh)" 
```
***

#### Or use `wget`
```shell
bash -c "$(wget -O- https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install.sh)" 
```
***

#### Or do it manually (not recommended)
  
Download [maven2sbt-cli-1.4.0.zip](https://github.com/Kevin-Lee/maven2sbt/releases/download/v1.4.0/maven2sbt-cli-1.4.0.zip) and unzip it.
  
Add an alias for convenience to `~/.zshrc` or `~/.bashrc` or `~/.bash_profile` or the run commands file for your shell. 
```shell
alias maven2sbt='/path/to/maven2sbt-cli-1.4.0/bin/maven2sbt'
```


### Windows
#### Install GraalVM Native Image (Recommended)


Download maven2sbt-cli-windows-latest.exe blow. Sorry, there's no installation script for Windows yet.

:::tip Recommended
Download: [maven2sbt-cli-windows-latest.exe](https://github.com/Kevin-Lee/maven2sbt/releases/download/v1.4.0/maven2sbt-cli-windows-latest.exe)
:::

#### Java Package (JVM)
Download and unzip the `maven2sbt-cli-1.4.0.zip` just like Linux or macOS.

You can run `maven2sbt-cli-1.4.0/bin/maven2sbt.bat` file but it hasn't been tested.
