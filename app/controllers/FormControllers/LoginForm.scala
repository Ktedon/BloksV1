package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class LoginForm(email: String, password: String)

object LoginForm {
	val form: Form[LoginForm] = Form(
		mapping(
			  "email"    -> email
			, "password" -> nonEmptyText(1, 149)
		)(LoginForm.apply)(LoginForm.unapply)
	)
}
