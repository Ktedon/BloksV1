package model

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import models.Tables._

class GroupModel(db: Database)(implicit ec: ExecutionContext) {

  def getGroups(schoolID: Int): Future[Seq[BlokGroupRow]] =
    db.run(
      BlokGroup.filter(_.school === schoolID).result
    )
  def getGroup(groupId: Int): Future[Seq[BlokGroupRow]] =
    db.run(
      BlokGroup.filter(_.id === groupId).result
    )

  def createGroup(
      blokId: Int,
      groupName: String,
      groupType: Short
  ): Future[Boolean] =
    if (SQL.injectionCheck(groupName))
      Future.successful(false)
    else
      db.run(
        BlokGroup += BlokGroupRow(
          -1,
          blokId,
          groupName,
          groupType,
          s"https://avatars.dicebear.com/api/jdenticon/${scala.util.Random.nextInt}.svg"
        )
      ).map(_ > 0)

  def getGroupsMembership(
      userId: Int
  ): Future[Seq[GroupMembershipRow]] =
    db.run(
      GroupMembership
        .filter(_.userId === userId)
        .result
    )

  def addGroupsMembership(
      userId: Int,
      groupID: Int
  ): Future[Boolean] =
    db.run(
      GroupMembership += GroupMembershipRow(-1, groupID, userId, 0)
    ).map(_ > 0)

  def deleteGroupsMembership(
      userId: Int,
      groupID: Int
  ): Future[Boolean] =
    db.run(
      GroupMembership
        .filter(groupMembership =>
          groupMembership.groupId === groupID &&
            groupMembership.userId === userId
        )
        .delete
    ).map(_ > 0)

  def getGroupThreads(
      groupID: Int
  ): Future[Seq[GroupThreadRow]] =
    db.run(
      GroupThread.filter(_.groupId === groupID).result
    )

  def addGroupThread(
      groupID: Int,
      parentID: Int,
      title: String,
      text: String,
      userId: Int
  ): Future[Boolean] =
    if (SQL.injectionCheck(title, text))
      Future.successful(false)
    else
      db.run(
        GroupThread += GroupThreadRow(
          -1,
          groupID,
          userId,
          parentID,
          title,
          text,
          new java.sql.Timestamp(System.currentTimeMillis())
        )
      ).map(_ > 0)

  def getThreadsAndUsers(groupId: Int): Future[Seq[(GroupThreadRow, BloksUserRow)]] =
    db.run(
      (for {
        thread <- GroupThread if thread.groupId === groupId
        user   <- BloksUser   if user.id === thread.userId
      } yield {
        (thread, user)
      }).result
    )
}
