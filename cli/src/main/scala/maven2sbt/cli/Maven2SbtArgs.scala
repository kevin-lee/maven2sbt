package maven2sbt.cli

import maven2sbt.core.ScalaVersion

/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
case class Maven2SbtArgs(scalaVersion: ScalaVersion, pomPath: String)
