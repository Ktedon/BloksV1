package controllers

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class ChangeBiographyForm(newBio: String)

object ChangeBiographyForm {
  val form: Form[ChangeBiographyForm] = Form(
    mapping(
      "newBio" -> nonEmptyText(1, 2000)
    )(ChangeBiographyForm.apply)(ChangeBiographyForm.unapply)
  )
}
