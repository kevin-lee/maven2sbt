package maven2sbt.core

import maven2sbt.core.MavenProperty.findPropertyName

import scala.annotation.tailrec
import scala.util.matching.Regex

import cats.syntax.all._

/**
  * @author Kevin Lee
  * @since 2019-04-21
  */
sealed trait M2SStringOps {

  /**
   * @example
   *
   * &#36;{whatever-is-here.blahBlah}
   */
  val propertyUsagePattern: Regex = """\$\{([^\{^\}]+)\}""".r

  /**
   * @example
   * {{{
   *   capitalizeAfterIgnoringNonAlphaNumUnderscore("abc.def")
   *   // abcDef
   *
   *   capitalizeAfterIgnoringNonAlphaNumUnderscore("abc-def")
   *   // abcDef
   *
   *   capitalizeAfterIgnoringNonAlphaNumUnderscore("abc_def")
   *   // abc_def
   *
   *   capitalizeAfterIgnoringNonAlphaNumUnderscore("abc.def-ghi")
   *   // abcDefGhi
   *
   * }}}
   * @param value
   * @return
   */
  def capitalizeAfterIgnoringNonAlphaNumUnderscore(value: String): String = {
    def isKnownLetter(c: Char) = {
      c.isDigit || c.isUpper || c.isLower || c === '_'
    }

    @tailrec
    def toCapitalized(s: List[Char], shouldBeUpper: Boolean, acc: List[Char]): List[Char] = s match {
      case Nil =>
        acc

      case c :: Nil =>
        if (isKnownLetter(c))
          toCapitalized(Nil, false, (if (shouldBeUpper) c.toUpper else c) :: acc)
        else
          toCapitalized(Nil, false, acc)

      case c :: cs =>
        if (isKnownLetter(c))
          toCapitalized(cs, false, (if (shouldBeUpper) c.toUpper else c) :: acc)
        else
          toCapitalized(cs, true, acc)
    }
    val cs = value.headOption.map { c=>
        if (c.isUpper || c.isLower || c === '_')
          c.toString
        else
          s"_${c.toString}"
      }.fold(List.empty[Char])(_.toList) ++ value.toList.drop(1)
    toCapitalized(cs, false, List.empty[Char]).reverse.mkString
  }

  def toPropertyNameOrItself(propsName: Props.PropsName, name: String): String =
    findPropertyName(name)
      .fold(s""""$name"""")(dotSeparated =>
        s"${propsName.propsName}.${capitalizeAfterIgnoringNonAlphaNumUnderscore(dotSeparated)}"
      )

  def indent(size: Int): String = " " * size

}

object M2SStringOps extends M2SStringOps
