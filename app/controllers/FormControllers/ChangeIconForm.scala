package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangeIconForm(`type`: String, seed: String)

object ChangeIconForm {
	val form: Form[ChangeIconForm] = Form(
		mapping(
			"type"   -> nonEmptyText(1, 15)
			, "seed" -> nonEmptyText(1, 512)
		)(ChangeIconForm.apply)(ChangeIconForm.unapply)
	)
}
