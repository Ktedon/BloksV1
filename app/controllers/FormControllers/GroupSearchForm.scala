package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class GroupSearchForm(query: String)

object GroupSearchForm {
	val form: Form[GroupSearchForm] = Form(
		mapping(
			"query" -> nonEmptyText(1, 149)
		)(GroupSearchForm.apply)(GroupSearchForm.unapply)
	)
}
