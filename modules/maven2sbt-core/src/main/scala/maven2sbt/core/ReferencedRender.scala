package maven2sbt.core

/** @author Kevin Lee
  * @since 2020-03-14
  */
trait ReferencedRender[A] {
  def render(propsName: Props.PropsName, libsName: Libs.LibsName, libs: Libs, a: A): RenderedString
}

object ReferencedRender {
  def apply[A: ReferencedRender]: ReferencedRender[A] = implicitly[ReferencedRender[A]]

  def namedReferencedRender[A](
    name: String,
    f: (Props.PropsName, Libs.LibsName, Libs, A) => RenderedString,
  ): ReferencedRender[A] =
    NamedReferencedRender(name, f)

  final case class NamedReferencedRender[A](
    name: String,
    f: (Props.PropsName, Libs.LibsName, Libs, A) => RenderedString,
  ) extends ReferencedRender[A] {
    override def render(propsName: Props.PropsName, libsName: Libs.LibsName, libs: Libs, a: A): RenderedString =
      f(propsName, libsName, libs, a)
  }
}
