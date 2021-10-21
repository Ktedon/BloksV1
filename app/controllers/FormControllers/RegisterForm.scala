package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class RegisterForm(
	name: String,
	email: String,
	password: String,
	grade: String,
	relStatus: String,
	biography: String,
	dateOfBirth: String,
	showDOB: Boolean
)

object RegisterForm {
	val form: Form[RegisterForm] = Form(
		mapping(
			  "name"              -> nonEmptyText(1, 149)
			, "email"             -> email
			, "password"          -> nonEmptyText(1, 149)
			, "grade"             -> nonEmptyText(1, 149)
			, "relStatus"         -> nonEmptyText(1, 149)
			, "biography"         -> nonEmptyText(1, 2000)
			, "dateOfBirth"       -> nonEmptyText
			, "showDOB"           -> boolean
		)(RegisterForm.apply)(RegisterForm.unapply)
	)
}
