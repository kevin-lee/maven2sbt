package maven2sbt.core.syntax

import hedgehog.*
import hedgehog.runner.*
import maven2sbt.core.{Props, Render, RenderedString}

/** @author Kevin Lee
  * @since 2023-01-01
  */
object renderSpec extends Properties {
  override def tests: List[Test] = List(
    property("test A.render", testRender)
  )

  def testRender: Property =
    for {
      propsName <- Gen.string(Gen.alpha, Range.linear(1, 100)).map(Props.PropsName(_)).log("propsName")
      s         <- Gen.string(Gen.unicode, Range.linear(1, 100)).log("s")
    } yield {
      val something = Something(s)
      import maven2sbt.core.syntax.render.*
      val expected = RenderedString.withProps(s"${propsName.value}=>$s")
      val actual = something.render(propsName)
      actual ==== expected
    }

  final case class Something(s: String)
  object Something {
    implicit val somethingRender: Render[Something] =
      (propsName, something) => RenderedString.withProps(s"${propsName.value}=>${something.s}")
  }
}
