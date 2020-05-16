package maven2sbt.cli

import java.io.File

import maven2sbt.core.ScalaVersion

/**
 * @author Kevin Lee
 * @since 2019-12-08
 */
sealed trait Maven2SbtArgs

object Maven2SbtArgs {

  final case class FileArgs(
      scalaVersion: ScalaVersion
    , out: File
    , overwrite: Overwrite
    , pomPath: File
    ) extends Maven2SbtArgs

  final case class PrintArgs(
      scalaVersion: ScalaVersion
    , pomPath: File
    ) extends Maven2SbtArgs


  def fileArgs(
    scalaVersion: ScalaVersion
  , out: File
  , overwrite: Overwrite
  , pomPath: File
  ): Maven2SbtArgs = FileArgs(scalaVersion, out, overwrite, pomPath)

  def printArgs(
      scalaVersion: ScalaVersion
     , pomPath: File
     ): Maven2SbtArgs = PrintArgs(scalaVersion, pomPath)

}

sealed trait Overwrite

object Overwrite {
  case object DoOverwrite extends Overwrite
  case object DoNotOverwrite extends Overwrite

  def doOverwrite: Overwrite = DoOverwrite
  def doNotOverwrite: Overwrite = DoNotOverwrite

  def fromBoolean(overwrite: Boolean): Overwrite =
    if (overwrite) doOverwrite else doNotOverwrite
}
