package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangePasswordForm(newpwd: String)

object ChangePasswordForm {
	val form: Form[ChangePasswordForm] = Form(
		mapping(
			"newpwd" -> nonEmptyText(1, 99)
		)(ChangePasswordForm.apply)(ChangePasswordForm.unapply)
	)
}
