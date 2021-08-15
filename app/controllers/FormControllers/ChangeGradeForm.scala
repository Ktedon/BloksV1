package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangeGradeForm(newGrade: String)

object ChangeGradeForm {
	val form: Form[ChangeGradeForm] = Form(
		mapping(
			"newGrade" -> nonEmptyText(1, 49)
		)(ChangeGradeForm.apply)(ChangeGradeForm.unapply)
	)
}
