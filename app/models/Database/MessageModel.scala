package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.helpers.EmailHelpers._
import models.helpers.AuthHelpers._
import models.Tables._

class MessageModel(db: Database)(implicit ec: ExecutionContext) {

  def addMessage(from: Int, to: Int, message: String): Future[Boolean] =
    if (SQL.injectionCheck(message))
      Future.successful(false)
    else
      db.run(
        Message += MessageRow(
          -1,
          from,
          to,
          message,
          new java.sql.Timestamp(System.currentTimeMillis())
        )
      ).map(_ > 0)

  def getMessages(from: Int, to: Int): Future[Seq[MessageRow]] = {
    val myMessages =
      Message.filter(message => message.fromId === from && message.toId === to)
    val theirMessages =
      Message.filter(message => message.toId === from && message.fromId === to)
    db.run((myMessages union theirMessages).result)
  }
}
