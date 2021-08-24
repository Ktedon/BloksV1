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
      db.run((BloksUser.filter(_.id === from) union BloksUser.filter(_.id === to)).result).flatMap { users =>
        for (user <- users) {
          if (user.id == from)
            createContact(to, from, user.name, user.profilePic, user.electedRank, user.grade)
          else
            createContact(from, to, user.name, user.profilePic, user.electedRank, user.grade)
        }
        db.run(
          Message += MessageRow(
            -1,
            from,
            to,
            message,
            new java.sql.Timestamp(System.currentTimeMillis())
          )
        ).map(_ > 0)
      }

  def getMessages(from: Int, to: Int): Future[Seq[MessageRow]] = {
    val myMessages =
      Message.filter(message => message.fromId === from && message.toId === to)
    val theirMessages =
      Message.filter(message => message.toId === from && message.fromId === to)
    db.run((myMessages union theirMessages).result)
  }

  def getContacts(userId: Int): Future[Seq[ContactRow]] = {
    db.run(Contact.filter(contact => contact.userId === userId).result)
  }

  def deleteContact(contactId: Int, userId: Int): Future[Boolean] = {
    db.run(Contact.filter(contact =>
      contact.id === contactId && contact.userId === userId
    ).delete).map(_ > 0)
  }

  def createContact(userId: Int,
                    contactId: Int,
                    contactName: String,
                    contactProfilePic: String,
                    contactElectedRank: Short,
                    contactGrade: Int):
  Future[Boolean] = {
    db.run(Contact.filter(contact => contact.contactUserId === contactId && contact.userId === userId).result).flatMap { contactSeq =>
      if (contactSeq.isEmpty)
        db.run(Contact += ContactRow(-1, userId, contactId, contactName, contactProfilePic, contactElectedRank, contactGrade)).map(_ > 0)
      else
        Future.successful(false)
    }
  }
}
