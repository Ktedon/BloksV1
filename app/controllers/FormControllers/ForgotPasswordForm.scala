package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ForgotPasswordForm(email: String)

object ForgotPasswordForm {
	val form: Form[ForgotPasswordForm] = Form(
		mapping(
			"email"      -> nonEmptyText(1, 199)
		)(ForgotPasswordForm.apply)(ForgotPasswordForm.unapply)
	)
}
