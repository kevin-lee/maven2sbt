package maven2sbt.core

import cats.syntax.all.*

import scala.annotation.tailrec

/** @author Kevin Lee
  * @since 2020-12-26
  */
object TestUtils {

  /** Find and return range pairs from List[Int]
    * @example
    * {{{
    *   findRange(List(0)))
    *   // List((0,0))
    *
    *   findRange(List(0, 1, 2))
    *   // List((0,2))
    *
    *   findRange(List(0, 2))
    *   // List((0,0), (2,2))
    *
    *   findRange(
    *     List(
    *       0, 1, 2,
    *       4, 5,
    *       20, 21, 22, 23, 24, 25,
    *       35
    *     )
    *   )
    *   // List((0,2), (4,5), (20,25), (35,35))
    * }}}
    */
  def findRange(ns: List[Int]): List[(Int, Int)] = {
    @tailrec
    def collectRange(ns: List[Int], rangeFound: (Int, Int), acc: List[(Int, Int)]): List[(Int, Int)] =
      rangeFound match {
        case (start, end) =>
          ns match {
            case x :: xs =>
              if ((end + 1) === x)
                collectRange(xs, start -> x, acc)
              else
                collectRange(xs, x     -> x, rangeFound :: acc)
            case Nil =>
              rangeFound :: acc
          }
      }
    ns.headOption.toList.flatMap(start => collectRange(ns.drop(1), start -> start, Nil)).reverse
  }

  lazy val ExpectedLetters: List[(Int, Int)] =
    findRange(
      (0.toChar to Char.MaxValue)
        .filter(c => c.isUpper || c.isLower || c.isDigit || c === '_')
        .map(_.toInt)
        .toList
    )

  lazy val ExpectedNonDigitLetters: List[(Int, Int)] =
    findRange(
      (0.toChar to Char.MaxValue)
        .filter(c => c.isUpper || c.isLower || c === '_')
        .map(_.toInt)
        .toList
    )

  lazy val ExpectedNonLetters: List[(Int, Int)] =
    findRange(
      (0.toChar to Char.MaxValue)
        .filterNot(c => c.isUpper || c.isLower || c.isDigit || c === '_')
        .map(_.toInt)
        .toList
    )

  val NonWhitespaceCharRange: List[(Int, Int)] =
    List(
      0     -> 8,
      14    -> 27,
      33    -> 5759,
      5761  -> 8191,
      8199  -> 8199,
      8203  -> 8231,
      8234  -> 8286,
      8288  -> 12287,
      12289 -> Char.MaxValue.toInt
    )

}
