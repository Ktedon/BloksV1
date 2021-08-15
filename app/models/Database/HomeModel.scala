package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.helpers.EmailHelpers._
import models.helpers.AuthHelpers._
import models.PublicUser
import models.Tables._

class HomeModel(db: Database)(implicit ec: ExecutionContext) {

  def homeSearch(
      blokId: Int,
      query: String
  ): Future[Tuple2[Seq[BlokGroupRow], Seq[PublicUser]]] =

    if (SQL.injectionCheck(query))
      Future.successful((Seq.empty, Seq.empty))
    else
      db.run(
        BlokGroup.filter(_.school === blokId).result
      ).map(
        _.filter(
          _.name.toUpperCase.contains(query.toUpperCase)
        )
      ).flatMap { bloks =>
        db.run(
          BloksUser.filter(_.blokId === blokId).result
        ).map(
          _.filter(
            _.name.toUpperCase.contains(query.toUpperCase)
          )
        ).map { users =>
          Tuple2(bloks, PublicUser.publicUserApply(users))
        }
      }
}
