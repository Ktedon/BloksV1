package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangeGenderForm(newGender: String)

object ChangeGenderForm {
	val form: Form[ChangeGenderForm] = Form(
		mapping(
			"newGender" -> nonEmptyText(1, 49)
		)(ChangeGenderForm.apply)(ChangeGenderForm.unapply)
	)
}
