package maven2sbt.core

import scala.util.matching.Regex

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
sealed trait Common {

  val dotSeparatedPattern: Regex = """\$\{(.+)\}""".r

  def dotSeparatedToCamelCase(dotSeparated: String): String = {
    val names = dotSeparated.trim.split("\\.")
    if (names.length == 1)
      dotSeparated
    else
      names.head + names.tail.map(_.capitalize).mkString
  }

  def indent(size: Int): String = " " * size

}

object Common extends Common
