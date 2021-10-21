package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.Tables._

class SettingModel(db: Database)(implicit ec: ExecutionContext) {

  def changeIcon(userId: Int, newIcon: String): Future[Boolean] =
    if (SQL.injectionCheck(newIcon))
      Future.successful(false)
    else
      db.run(
        (
          for {
            user <- BloksUser if user.id === userId
          } yield user.profilePic
        ).update(newIcon)
      ).map(_ > 0)

  def changePassword(userId: Int, newPassword: String): Future[Boolean] =
    if (SQL.injectionCheck(newPassword))
      Future.successful(false)
    else
      db.run(
        (
          for {
            user <- BloksUser if user.id === userId
          } yield user.password
        ).update(newPassword)
      ).map(_ > 0)

  def deleteAccount(userId: Int): Future[Boolean] = {
    val queries = Seq(
      BloksUser.filter(_.id === userId),
      GroupThread.filter(_.userId === userId),
      Message.filter(msg => msg.fromId === userId || msg.toId === userId),
      Notification.filter(notif =>
        notif.toId === userId || notif.fromId === userId
      )
    )
    queries.map(query => db.run(query.delete))
    queries
      .foldLeft(Future.successful(0)) { (left, query) =>
        left.flatMap { leftNum =>
          db.run(query.result).map(_.length + leftNum)
        }
      }
      .map(_ > 0)
  }

  // def changeGender(userId: Int, newGender: String): Future[Boolean] =
  //   if (SQL.injectionCheck(newGender))
  //     Future.successful(false)
  //   else
  //     db.run(
  //       (
  //         for {
  //           user <- BloksUser if user.id === userId
  //         } yield user.gender
  //       ).update(newGender)
  //     ).map(_ > 0)

  // def changeGenderVisibility(
  //     userId: Int,
  //     newVisibility: Boolean
  // ): Future[Boolean] =
  //   db.run(
  //     (
  //       for {
  //         user <- BloksUser if user.id === userId
  //       } yield user.showGender
  //     ).update(newVisibility)
  //   ).map(_ > 0)

  // def changeSex(userId: Int, newSex: String): Future[Boolean] =
  //   if (SQL.injectionCheck(newSex))
  //     Future.successful(false)
  //   else
  //     db.run(
  //       (
  //         for {
  //           user <- BloksUser if user.id === userId
  //         } yield user.biologicalSex
  //       ).update(newSex)
  //     ).map(_ > 0)

  // def changeSexVisibility(
  //     userId: Int,
  //     newVisibility: Boolean
  // ): Future[Boolean] =
  //   db.run(
  //     (
  //       for {
  //         user <- BloksUser if user.id === userId
  //       } yield user.showBiologicalSex
  //     ).update(newVisibility)
  //   ).map(_ > 0)

  def changeGrade(userId: Int, newGrade: Int): Future[Boolean] =
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.grade
      ).update(newGrade)
    ).map(_ > 0)

  def changeRelStatus(userId: Int, newStatus: String): Future[Boolean] =
    if (SQL.injectionCheck(newStatus))
      Future.successful(false)
    else
      db.run(
        (
          for {
            user <- BloksUser if user.id === userId
          } yield user.relationshipStatus
        ).update(newStatus)
      ).map(_ > 0)

  def changeBiography(userId: Int, newBiography: String): Future[Boolean] =
    if (SQL.injectionCheck(newBiography))
      Future.successful(false)
    else
      db.run(
        (
          for {
            user <- BloksUser if user.id === userId
          } yield user.biography
        ).update(newBiography)
      ).map(_ > 0)
}