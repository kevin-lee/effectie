package effectie.core

enum YesNo derives CanEqual {
  case Yes
  case No
}
object YesNo {
  def yes: YesNo = Yes
  def no: YesNo = No
}
