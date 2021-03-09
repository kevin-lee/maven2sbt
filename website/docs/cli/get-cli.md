---
id: 'get-cli'
title: 'Get maven2sbt CLI'
sidebar_label: 'Get CLI'
---


## Standalone CLI App

It requires Java 11 or higher. So JRE should be installed and available to run `maven2sbt-cli`.

### Debian / Ubuntu Linux
If you use Debian or Unbuntu Linux you can download [maven2sbt-cli_1.2.0_all.deb](https://github.com/Kevin-Lee/maven2sbt/releases/download/v1.2.0/maven2sbt-cli_1.2.0_all.deb) and install it.
```shell
$ dpkg -i maven2sbt-cli_1.2.0_all.deb 
```
`maven2sbt` should be available.
e.g.)
```shell
$ which maven2sbt
/usr/bin/maven2sbt
```


### Linux / macOS
#### Use `curl`
```shell
sh -c "$(curl -fsSL https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install.sh)" 
```
***

#### Or use `wget`
```shell
sh -c "$(wget -O- https://raw.githubusercontent.com/Kevin-Lee/maven2sbt/main/.scripts/install.sh)" 
```
***

#### Or do it manually (not recommended)
  
Download [maven2sbt-cli-1.2.0.zip](https://github.com/Kevin-Lee/maven2sbt/releases/download/v1.2.0/maven2sbt-cli-1.2.0.zip) or [maven2sbt-cli-1.2.0.tgz](https://github.com/Kevin-Lee/maven2sbt/releases/download/v1.2.0/maven2sbt-cli-1.2.0.tgz) and unzip it.
  
Add an alias for convenience to `~/.zshrc` or `~/.bashrc` or `~/.bash_profile` or the run commands file for your shell. 
```shell
alias maven2sbt='/path/to/maven2sbt-cli-1.2.0/bin/maven2sbt'
```


### Windows

Download and unzip the `maven2sbt-cli-1.2.0.zip` or `maven2sbt-cli-1.2.0.tgz` just like Linux or macOS.

You can run `maven2sbt-cli-1.2.0/bin/maven2sbt.bat` file but it hasn't been tested.
