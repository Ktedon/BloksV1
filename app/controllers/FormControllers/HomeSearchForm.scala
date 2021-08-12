package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class HomeSearchForm(query: String)

object HomeSearchForm {
	val form: Form[HomeSearchForm] = Form(
		mapping(
			"query"  -> nonEmptyText(1, 199)
		)(HomeSearchForm.apply)(HomeSearchForm.unapply)
	)
}