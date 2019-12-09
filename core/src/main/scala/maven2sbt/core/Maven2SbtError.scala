package maven2sbt.core

import java.io.File

/**
 * @author Kevin Lee
 * @since 2019-12-09
 */
sealed trait Maven2SbtError

object Maven2SbtError {

  final case class PomFileNotExist(pomFile: File) extends Maven2SbtError

  def pomFileNotExist(pomFile: File): Maven2SbtError = PomFileNotExist(pomFile)

  def render(maven2SbtError: Maven2SbtError): String = maven2SbtError match {
    case PomFileNotExist(pomFile) =>
      s"No pom file found at ${pomFile.getCanonicalPath}."
  }

}
