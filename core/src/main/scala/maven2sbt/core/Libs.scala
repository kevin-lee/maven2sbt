package maven2sbt.core

import io.estatico.newtype.macros.newtype

import scala.language.implicitConversions
import scala.xml.Node

/**
 * @author Kevin Lee
 * @since 2021-03-04
 */
final case class Libs(dependencies: List[(Libs.LibValName, Dependency)])

object Libs {
  @newtype case class LibsName(libsName: String)
  
  @newtype case class LibValName(libValName: String)

  def toValName(dependency: Dependency): LibValName =
    LibValName(StringUtils.capitalizeAfterIgnoringNonAlphaNumUnderscore(dependency.artifactId.artifactId))
  
  def from(pom: Node): Libs =
    Libs(
      (for {
        dependencies <- (pom \ "dependencyManagement")
        dependency <- Dependency.from(dependencies)
      } yield (toValName(dependency), dependency)).toList
    )


  def render(propsName: Props.PropsName, libsName: LibsName, indentSize: Int, libs: Libs): String = {
    val indent = StringUtils.indent(indentSize)
    libs.dependencies
      .map(Dependency.renderReusable(propsName, _))
        .map { case (name, libStr) =>
          s"${indent}val ${name.libValName} = ${libStr.toQuotedString}"
        }
      .stringsMkString(s"lazy val ${libsName.libsName} = new {\n", "\n", "\n}")

  }
}
