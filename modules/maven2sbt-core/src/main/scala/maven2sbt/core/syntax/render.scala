package maven2sbt.core.syntax

import maven2sbt.core.{Props, Render, RenderedString}

/** @author Kevin Lee
  * @since 2023-01-01
  */
object render {
  implicit class RenderSyntax[A](private val a: A) extends AnyVal {
    def render(propsName: Props.PropsName)(implicit render: Render[A]): RenderedString =
      render.render(propsName, a)
  }
}
