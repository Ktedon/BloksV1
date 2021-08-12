package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class RegisterForm(name: String, email: String, password: String, grade: String, relStatus: String, 
	gender: String, showGender: Boolean, biologicalSex: String, showBiologicalSex: Boolean, biography: String, 
	dateOfBirth: String, showDOB: Boolean)

object RegisterForm {
	val form: Form[RegisterForm] = Form(
		mapping(
			  "name" -> nonEmptyText(1, 149)
			, "email"  -> email
			, "password"  -> nonEmptyText(1, 149)
			, "grade"  -> nonEmptyText(1, 149)
			, "relStatus"  -> nonEmptyText(1, 149)
			, "gender"  -> nonEmptyText(1, 149)
			, "showGender"  -> boolean
			, "biologicalSex"  -> nonEmptyText(1, 149)
			, "showBiologicalSex"  -> boolean
			, "biography"  -> nonEmptyText(1, 2000)
			, "dateOfBirth"  -> nonEmptyText
			, "showDOB" -> boolean
			// , "extraDataJSON" -> text
		)(RegisterForm.apply)(RegisterForm.unapply)
	)
}