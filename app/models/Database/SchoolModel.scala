package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.helpers.EmailHelpers._
import models.Tables._

class SchoolModel(db: Database)(implicit ec: ExecutionContext) {
  def getSchools(): Future[Seq[BloksSchoolRow]] = {
    db.run(BloksSchool.result)
  }

  def addSchool(
      name: String,
      email: String,
      population: Short,
      state: String,
      schoolRequirements: Short,
      schoolFunding: Short,
      dateOfRegistration: java.sql.Date
  ): Future[Boolean] =
    if (SQL.injectionCheck(name, email, state))
      Future.successful(false)
    else
      db.run(
        BloksSchool += BloksSchoolRow(
          -1,
          name,
          getEmailExtension(email),
          population,
          state,
          schoolRequirements,
          schoolFunding,
          dateOfRegistration
        )
      ).map(_ > 0)
}
