package maven2sbt.effect

/**
 * @author Kevin Lee
 * @since 2020-02-24
 */
sealed trait YesNo

object YesNo {

  case object Yes extends YesNo
  case object No extends YesNo

  def yes: YesNo = Yes
  def no: YesNo = No

}
