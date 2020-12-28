package maven2sbt.core

sealed trait RenderedString

object RenderedString {
  final case class WithProps(withProps: String) extends RenderedString
  final case class WithoutProps(withoutProps: String) extends RenderedString
  final case class NoQuotesRequired(noQuotesRequired: String) extends RenderedString

  def withProps(withProps: String): RenderedString =
    WithProps(withProps)

  def withoutProps(withoutProps: String): RenderedString =
    WithoutProps(withoutProps)

  def noQuotesRequired(noQuotesRequired: String): RenderedString =
    NoQuotesRequired(noQuotesRequired)

}
