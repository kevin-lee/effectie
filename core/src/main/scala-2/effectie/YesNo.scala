package effectie

sealed trait YesNo

object YesNo {

  case object Yes extends YesNo
  case object No extends YesNo

  def yes: YesNo = Yes
  def no: YesNo  = No

}
