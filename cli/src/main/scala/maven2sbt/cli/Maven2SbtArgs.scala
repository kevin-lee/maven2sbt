package maven2sbt.cli

import java.io.File

import maven2sbt.core.ScalaVersion

/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
case class Maven2SbtArgs(scalaVersion: ScalaVersion, out: File, overwrite: Overwrite, pomPath: File)

sealed trait Overwrite

object Overwrite {
  final case object DoOverwrite extends Overwrite
  final case object DoNotOverwrite extends Overwrite

  def doOverwrite: Overwrite = DoOverwrite
  def doNotOverwrite: Overwrite = DoNotOverwrite

  def fromBoolean(overwrite: Boolean): Overwrite =
    if (overwrite) doOverwrite else doNotOverwrite
}
