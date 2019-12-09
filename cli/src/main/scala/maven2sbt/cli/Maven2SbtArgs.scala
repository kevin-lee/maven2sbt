package maven2sbt.cli

import java.io.File

import maven2sbt.core.ScalaVersion

/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
case class Maven2SbtArgs(scalaVersion: ScalaVersion, out: File, pomPath: File)
