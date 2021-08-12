package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangeSexForm(newSex: String)

object ChangeSexForm {
	val form: Form[ChangeSexForm] = Form(
		mapping(
			"newSex"  -> nonEmptyText(1, 49)
		)(ChangeSexForm.apply)(ChangeSexForm.unapply)
	)
}
