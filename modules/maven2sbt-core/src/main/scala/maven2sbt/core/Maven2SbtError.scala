package maven2sbt.core

import java.io.File

/** @author Kevin Lee
  * @since 2019-12-09
  */
sealed trait Maven2SbtError

object Maven2SbtError {

  final case class PomFileNotExist(pomFile: File) extends Maven2SbtError
  case object NoPomInputStream extends Maven2SbtError
  final case class OutputFileAlreadyExist(output: File) extends Maven2SbtError

  def pomFileNotExist(pomFile: File): Maven2SbtError       = PomFileNotExist(pomFile)
  def noPomInputStream: Maven2SbtError                     = NoPomInputStream
  def outputFileAlreadyExist(output: File): Maven2SbtError = OutputFileAlreadyExist(output)

  def render(maven2SbtError: Maven2SbtError): String = maven2SbtError match {
    case PomFileNotExist(pomFile) =>
      s"No pom file found at ${pomFile.getCanonicalPath}"

    case NoPomInputStream =>
      s"No pom InputStream. It is null."

    case OutputFileAlreadyExist(output) =>
      s"Output file already exists at ${output.getCanonicalPath}"
  }

}
