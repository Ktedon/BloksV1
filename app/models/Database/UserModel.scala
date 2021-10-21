package model

import slick.jdbc.PostgresProfile.api._
import ky.korins.blake3.Blake3

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.helpers.EmailHelpers._
import models.helpers.AuthHelpers._
import models.Tables.BloksUserRow
import models.Tables._

class UserModel(db: Database)(implicit ec: ExecutionContext) {

  def validateUser(
      email: String,
      password: String
  ): Future[Option[BloksUserRow]] =
    if (SQL.injectionCheck(email, password))
      Future.successful(None)
    else
      db.run(
        BloksUser
          .filter(users =>
            users.email === email &&
              users.password === Blake3.hex(password, 64) &&
              users.isValidated === true
          )
          .result
      ).map {
        _.headOption match {
          case Some(userFound) =>
            Option(userFound)
          case None =>
            None
        }
      }

  def validateUserNoEmailVerification(
      email: String,
      password: String
  ): Future[Option[BloksUserRow]] =
    if (SQL.injectionCheck(email, password))
      Future.successful(None)
    else
      db.run(
        BloksUser
          .filter(users =>
            users.email === email &&
              users.password === Blake3.hex(password, 64)
          )
          .result
      ).map {
        _.headOption match {
          case Some(userFound) =>
            Option(userFound)
          case None =>
            None
        }
      }

  def validateEmail(email: String): Future[Int] =
    db.run(BloksUser.filter(user => user.email === email && user.isValidated === true).result).flatMap { userSeq =>
      if (userSeq.isEmpty)
        db.run(BloksUser.filter(_.email === email).result).map { userSeq2 =>
          if (userSeq2.isEmpty)
            -1
          else
            1
        }
      else
        Future.successful(0)
    }

  def validateUserByID(Id: Int): Future[Option[BloksUserRow]] =
    db.run(BloksUser.filter(userS => userS.id === Id).result)
      .map {

        _.headOption match {
          case Some(userFound) =>
            if (userFound.isValidated)
              Option(userFound)
            else
              None
          case None =>
            None
        }
      }

  def addUser(
      data: controllers.RegisterForm
  )(implicit mailer: play.api.libs.mailer.MailerClient): Future[Boolean] =
    if (
      SQL.injectionCheck(
        data.name,
        data.email,
        data.relStatus,
        data.biography,
        data.password
      ) /*|| (getEmailExtension(data.email) != "vt.ewsd.org")*/
    )
      Future.successful(false)
    else
      db.run(BloksUser.filter(user => user.email.toUpperCase === data.email.toUpperCase).result)
        .flatMap { userSeq =>
          if (userSeq.isEmpty)
            db.run(
              BloksUser += BloksUserRow(
                -1,
                1,
                false,
                data.name,
                data.email,
                0,
                Blake3.hex(data.password, 64),
                data.grade.toInt,
                data.relStatus,
                data.biography,
                new java.sql.Date(System.currentTimeMillis()).toString,
                s"https://avatars.dicebear.com/api/jdenticon/${scala.util.Random.nextInt}.svg"
              )
            ).map(_ > 0).flatMap { wasCreated =>
              if (wasCreated == false)
                Future.successful(false)
              else
                db.run(BloksUser.filter(user => user.email.toUpperCase === data.email.toUpperCase).result).map { userSeq =>
                  if (userSeq.isEmpty)
                    false
                  else
                    sendEmail(getEmailValidationKey(userSeq.head.id), data.email)
                    true
                }
            }
          else
            Future.successful(false)
        }

  def modifyUserValidation(email: String, password: String): Future[Boolean] =
    if (SQL.injectionCheck(email, password))
      Future.successful(false)
    else
      db.run(
        (
          for {
            user <- BloksUser
            if user.email === email && user.password === Blake3
              .hex(password, 64)
          } yield user.isValidated
        ).update(true)
      ).map(_ > 0)
}
