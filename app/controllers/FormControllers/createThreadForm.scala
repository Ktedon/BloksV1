package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ThreadCreateForm(title: String, text: String, parentID: String)

object ThreadCreateForm {
	val form: Form[ThreadCreateForm] = Form(
		mapping(
			"title"  -> nonEmptyText(1, 199)
			, "text"  -> nonEmptyText(1, 50000)
			, "parentID"  -> nonEmptyText(1, 12)
		)(ThreadCreateForm.apply)(ThreadCreateForm.unapply)
	)
}