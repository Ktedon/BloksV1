package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.Tables._

class NotificationModel(db: Database)(implicit ec: ExecutionContext) {

  def getNotifications(
      userId: Int
  ): Future[Seq[NotificationRow]] =
    db.run(Notification.filter(notif => notif.toId === userId).result)

  def addNotification(
      userId: Int,
      toId: Int,
      `type`: Short,
      fromUserName: String,
      fromUserEmail: String,
      fromUserProfilePic: String
  ): Future[Boolean] =
    if (
      SQL.injectionCheck(
        fromUserName,
        fromUserEmail,
        fromUserProfilePic
      )
    )
      Future.successful(false)
    else
      db.run(
        Notification += NotificationRow(
          -1,
          toId,
          `type`,
          userId,
          new java.sql.Time(System.currentTimeMillis()),
          fromUserName,
          fromUserEmail,
          fromUserProfilePic
        )
      ).map(_ > 0)

  def deleteNotification(
      notifID: Int
  ): Future[Boolean] =
    db.run(Notification.filter(_.id === notifID).delete)
      .map(_ > 0)

  def pokeUser(
      from: Int,
      fromUserName: String,
      fromUserEmail: String,
      fromUserProfilePic: String,
      toUser: Int
  ): Future[Boolean] =
    if (
      SQL.injectionCheck(
        fromUserName,
        fromUserEmail,
        fromUserProfilePic
      )
    )
      Future.successful(false)
    else
      db.run(
        Notification += NotificationRow(
          -1,
          toUser,
          4,
          from,
          new java.sql.Time(System.currentTimeMillis()),
          fromUserName,
          fromUserEmail,
          fromUserProfilePic
        )
      ).map(_ > 0)
}
