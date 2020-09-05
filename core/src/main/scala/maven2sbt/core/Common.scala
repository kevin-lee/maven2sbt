package maven2sbt.core

import scala.util.matching.Regex

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
sealed trait Common {

  /**
   * @example
   *
   * &#36;{whatever-is-here.blahBlah}
   */
  val propertyUsagePattern: Regex = """\$\{(.+)\}""".r

  def dotHyphenSeparatedToCamelCase(dotSeparated: String): String =
    dotSeparated.trim.split("[\\.-]+")  match {
      case Array(head, tail @ _*) => head + tail.map(_.capitalize).mkString

      case Array(_) => dotSeparated

      case Array() => ""
    }

  def indent(size: Int): String = " " * size

}

object Common extends Common
