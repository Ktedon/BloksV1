package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class CreateMessageForm(toID: Int, message: String)

object CreateMessageForm {
	val form: Form[CreateMessageForm] = Form(
		mapping(
			"toID"  -> number(1, 10000000)
			, "message"  -> nonEmptyText(1, 49999)
		)(CreateMessageForm.apply)(CreateMessageForm.unapply)
	)
}