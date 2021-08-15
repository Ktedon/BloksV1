package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangeRelStatusForm(newStatus: String)

object ChangeRelStatusForm {
	val form: Form[ChangeRelStatusForm] = Form(
		mapping(
			"newStatus" -> nonEmptyText(1, 99)
		)(ChangeRelStatusForm.apply)(ChangeRelStatusForm.unapply)
	)
}
