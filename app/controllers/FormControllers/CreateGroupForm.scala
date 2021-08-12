package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class GroupCreateForm(name: String, `type`: String)

object GroupCreateForm {
	val form: Form[GroupCreateForm] = Form(
		mapping(
			"name"  -> nonEmptyText(1, 199)
			, "type"  -> nonEmptyText(1, 3)
		)(GroupCreateForm.apply)(GroupCreateForm.unapply)
	)
}