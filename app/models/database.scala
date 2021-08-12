package model

import slick.jdbc.PostgresProfile.api._
import ky.korins.blake3.Blake3

import scala.concurrent.ExecutionContext
import models.Tables._
import models.PublicUser

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import java.sql.Date
import scala.reflect.ClassTag

class DatabaseModel(db: Database)(implicit ec: ExecutionContext) {

  @inline
  def getEmailExtension(email: String): String = {

    var extension = ""
    var counter = email.length() - 1

    while (email(counter) != '@') {
      extension += email(counter)
      counter -= 1
    }
    return extension.reverse
  }

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
  ): Future[Option[Int]] = {

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
    ).flatMap { addCount =>
      if (addCount > 0) {
        db.run(
          BloksSchool
            .filter(SchoolsRow =>
              SchoolsRow.emailExtension === getEmailExtension(email)
            )
            .result
        ).map(_.headOption.map(_.id))
      } else {
        Future.successful(None)
      }
    }
    Future.successful(None)
  }

  def validateUser(
      email: String,
      password: String
  ): Future[Option[BloksUserRow]] = {

    db.run(
      BloksUser
        .filter(userRows =>
          userRows.email === email &&
            userRows.password === Blake3.hex(password, 64)
        )
        .result
    ).map { userSeq =>
      userSeq.headOption match {
        case Some(userFound) =>
          if (userFound.isValidated) {
            Option(userFound)
          } else {
            None
          }

        case None =>
          None
      }
    }
  }

  def validateUserNoEmailVerification(
      email: String,
      password: String
  ): Future[Option[BloksUserRow]] = {

    db.run(
      BloksUser
        .filter(userRows =>
          userRows.email === email &&
            userRows.password === Blake3.hex(password, 64)
        )
        .result
    ).map { userSeq =>
      userSeq.headOption match {
        case Some(userFound) =>
          Option(userFound)

        case None =>
          None
      }
    }
  }

  def modifyUserValidation(email: String, password: String): Future[Boolean] = {
    db.run(
      BloksUser
        .filter(userRows =>
          userRows.email === email &&
            userRows.password === Blake3.hex(password, 64)
        )
        .result
    ).flatMap { userSeq =>
      userSeq.headOption match {
        case Some(userFound) =>
          if (userFound.isValidated) {
            Future.successful(true)
          } else {

            deleteUser(email, password)

            db.run(
              BloksUser += BloksUserRow(
                -1,
                1,
                true,
                userFound.name,
                userFound.email,
                userFound.electedRank,
                userFound.password,
                userFound.grade,
                userFound.relationshipStatus,
                userFound.gender,
                userFound.showGender,
                userFound.biologicalSex,
                userFound.showBiologicalSex,
                userFound.biography,
                userFound.dateOfRegistration,
                userFound.profilePic
              )
            ).flatMap { addCount =>
              if (addCount > 0) {
                db.run(
                  BloksUser
                    .filter(usersRow =>
                      usersRow.email === email &&
                        usersRow.password === Blake3.hex(password, 64)
                    )
                    .result
                ).map { french =>
                  french.headOption match {
                    case Some(thingFound) =>
                      db.run(
                        Notification += NotificationRow(
                          -1,
                          thingFound.id,
                          0,
                          0,
                          new java.sql.Time(System.currentTimeMillis()),
                          "eXul Team",
                          "eXul Team",
                          "https://avatars.dicebear.com/api/jdenticon/sff.svg"
                        )
                      )
                    case None => None
                  }
                  true
                }
              } else {
                Future.successful(false)
              }
            }

          }

        case None =>
          Future.successful(false)
      }
    }
  }

  def validateUserByID(Id: Int): Future[Option[BloksUserRow]] = {

    db.run(BloksUser.filter(userRows => userRows.id === Id).result)
      .map { userSeq =>
        userSeq.headOption match {
          case Some(userFound) =>
            if (userFound.isValidated) {
              Option(userFound)
            } else {
              None
            }

          case None =>
            None
        }
      }
  }

  // @inline
  // def validateUser(email: String, password: String): Future[Option[BloksUserRow]] = {

  //   db.run(BloksUser.filter(user => user.email === email && user.password === password).result)
  //     .map(user => user.headOption)
  // }

  // @inline
  // def validateUserByID(id: Int): Future[Option[BloksUserRow]] = {
  //   db.run(BloksUser.filter(user => user.id === id).result).map { user =>

  //     user.headOption
  //   }
  // }

  def addUser(data: controllers.RegisterForm): Future[Option[BloksUserRow]] = {
    validateUserNoEmailVerification(data.email, Blake3.hex(data.password, 64))
      .flatMap { user =>
        if (user.isEmpty) {
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
              data.gender,
              data.showGender,
              data.biologicalSex,
              data.showBiologicalSex,
              data.biography,
              new java.sql.Date(System.currentTimeMillis()),
              "https://avatars.dicebear.com/api/jdenticon/sfjrf.svg"
            )
          ).flatMap { addCount =>
            if (addCount > 0) {
              db.run(
                BloksUser
                  .filter(usersRow =>
                    usersRow.email === data.email &&
                      usersRow.password === Blake3.hex(data.password, 64)
                  )
                  .result
              ).map { french =>
                french.headOption
              }
            } else {
              Future.successful(None)
            }
          }
        } else {
          return Future.successful(None)
        }
      }
  }

  def deleteUser(email: String, password: String): Future[Boolean] = {
    db.run(
      BloksUser
        .filter(userRows =>
          userRows.email === email &&
            userRows.password === Blake3.hex(password, 64)
        )
        .delete
    ).map(count => count > 0)
  }

  def getNotifications(
      email: String,
      password: String
  ): Future[Seq[NotificationRow]] = {

    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(Notification.filter(notif => notif.toId === found.id).result)

        case None => Future(Seq())
      }
    }
  }

  def addNotification(
      email: String,
      password: String,
      toId: Int,
      `type`: Short,
      fromUserName: String,
      fromUserEmail: String,
      fromUserProfilePic: String
  ): Future[Boolean] = {

    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(
            Notification += NotificationRow(
              -1,
              toId,
              `type`,
              found.id,
              new java.sql.Time(System.currentTimeMillis()),
              fromUserName,
              fromUserEmail,
              fromUserProfilePic
            )
          ).map(addNum => addNum > 0)

        case None => Future(false)
      }
    }
  }

  def deleteNotification(
      email: String,
      password: String,
      notifID: Int
  ): Future[Boolean] = {
    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(Notification.filter(notif => notif.id === notifID).delete)
            .map(count => count > 0)

        case None => Future(false)
      }
    }
  }

  def homeSearch(
      email: String,
      password: String,
      query: String
  ): Future[Tuple2[Seq[BlokGroupRow], Seq[PublicUser]]] = {

    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(
            BlokGroup.filter(group => group.school === found.blokId).result
          ).map(
            _.filter(thing =>
              thing.name.toUpperCase.contains(query.toUpperCase)
            )
          ).flatMap { bloks =>
            db.run(
              BloksUser.filter(person => person.blokId === found.blokId).result
            ).map(item =>
              item.filter(thing =>
                thing.name.toUpperCase.contains(query.toUpperCase)
              )
            ).map { users =>
              Tuple2(bloks, PublicUser.publicUserApply(users))
            }
          }

        case None => Future(Tuple2(Seq.empty, Seq.empty))
      }

    }
  }

  def getGroups(email: String, password: String): Future[Seq[BlokGroupRow]] = {
    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(
            BlokGroup.filter(group => group.school === found.blokId).result
          )

        case None => Future(Seq.empty)
      }
    }
  }

  def createGroup(
      email: String,
      password: String,
      groupName: String,
      groupType: Short
  ): Future[Boolean] = {

    validateUser(email, password).flatMap { user =>
      user.headOption match {

        case Some(found) =>
          db.run(
            BlokGroup += BlokGroupRow(
              -1,
              found.blokId,
              groupName,
              groupType,
              s"https://avatars.dicebear.com/api/jdenticon/${scala.util.Random.nextInt}.svg"
            )
          ).map(addCount => addCount > 0)

        case None =>
          Future(false)
      }
    }
  }

  def getGroupsMembership(
      email: String,
      password: String
  ): Future[Seq[GroupMembershipRow]] = {
    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(
            GroupMembership
              .filter(membership => membership.userId === found.id)
              .result
          )

        case None => Future(Seq())
      }
    }
  }

  def addGroupsMembership(
      email: String,
      password: String,
      groupID: Int
  ): Future[Boolean] = {
    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(
            GroupMembership += GroupMembershipRow(-1, groupID, found.id, 0)
          ).map(addCount => addCount > 0)

        case None => Future(false)
      }
    }
  }

  def deleteGroupsMembership(
      email: String,
      password: String,
      groupID: Int
  ): Future[Boolean] = {
    validateUser(email, password).flatMap { user =>
      user match {
        case Some(found) =>
          db.run(
            GroupMembership
              .filter(groupMembership =>
                groupMembership.groupId === groupID &&
                  groupMembership.userId === found.id
              )
              .delete
          ).map(count => count > 0)

        case None => Future(false)
      }
    }
  }

  def getGroupThreads(
      groupID: Int
  ): Future[Seq[GroupThreadRow]] = {

      db.run(
        GroupThread.filter(thread => thread.groupId === groupID).result
      )

      // db.run(
      //   (
      //     for {
      //       thread <- GroupThread if thread.groupId === groupID
      //     } yield thread
      //   ).map { thread =>
      //     thread
      //   }
      // )


  }

  def addGroupThread(
      groupID: Int,
      parentID: Int,
      title: String,
      text: String,
      userId: Int
  ): Future[Boolean] = {

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
    ).map(addCount => addCount > 0)
  }

  def addMessage(from: Int, to: Int, message: String): Future[Boolean] = {
    db.run(
      Message += MessageRow(
        -1,
        from,
        to,
        message,
        new java.sql.Timestamp(System.currentTimeMillis())
      )
    ).map(addCount => addCount > 0)
  }

  def getMessages(from: Int, to: Int): Future[Seq[MessageRow]] = {
    db.run(
      Message
        .filter(message =>
          (message.fromId === from && message.toId === to)
            || (message.toId === from && message.fromId === to)
        )
        .result
    )
  }

  def pokeUser(
      from: Int,
      fromUserName: String,
      fromUserEmail: String,
      fromUserProfilePic: String,
      toUser: Int
  ): Future[Boolean] = {
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
    ).map { addCount =>
      if (addCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeIcon(userId: Int, newIcon: String): Future[Boolean] =
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.profilePic
      ).update(newIcon)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }

  def changePassword(userId: Int, newPassword: String): Future[Boolean] =
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.password
      ).update(newPassword)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }

  def deleteAccount(userId: Int): Future[Boolean] = {
    val queries = Seq(
      BloksUser.filter(user => user.id === userId),
      GroupThread.filter(thread => thread.userId === userId),
      Message.filter(msg => msg.fromId === userId || msg.toId === userId),
      Notification.filter(notif =>
        notif.toId === userId || notif.fromId === userId
      ),
      Friend.filter(friend =>
        friend.fromId === userId || friend.toId === userId
      )
    )
    queries.map(query => db.run(query.delete))
    queries
      .foldLeft(Future.successful(0)) { (left, query) =>
        left.flatMap { leftNum =>
          db.run(query.result).map(rightNum => leftNum + rightNum.length)
        }
      }
      .map(deleteCount => if (deleteCount > 0) true else false)
  }

  def changeGender(userId: Int, newGender: String): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.gender
      ).update(newGender)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeGenderVisibility(userId: Int, newVisibility: Boolean): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.showGender
      ).update(newVisibility)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeSex(userId: Int, newSex: String): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.biologicalSex
      ).update(newSex)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeSexVisibility(userId: Int, newVisibility: Boolean): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.showBiologicalSex
      ).update(newVisibility)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeGrade(userId: Int, newGrade: Int): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.grade
      ).update(newGrade)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeRelStatus(userId: Int, newStatus: String): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.relationshipStatus
      ).update(newStatus)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def changeBiography(userId: Int, newBiography: String): Future[Boolean] = {
    db.run(
      (
        for {
          user <- BloksUser if user.id === userId
        } yield user.biography
      ).update(newBiography)
    ).map { updateCount =>
      if (updateCount > 0) {
        true
      } else {
        false
      }
    }
  }

  def testEmailValidationKey(email: String, password: String, key: String): Future[Boolean] = {
    db.run(
      BloksUser
        .filter(userRows =>
          userRows.email === email &&
            userRows.password === Blake3.hex(password, 64)
        )
        .result
    ).map { userSeq =>
      userSeq.headOption match {
        case Some(userFound) =>
          if (Blake3.hex((~(((8 * userFound.id % 7) * 11) << 2) + 72).toString, 64) == key) {
            true
          } else {
            false
          }
        case None =>
          false
      }
    }
  }

  def getEmailValidationKey(email: String, password: String): Future[Option[String]] = {
    db.run(
      BloksUser
        .filter(userRows =>
          userRows.email === email &&
            userRows.password === Blake3.hex(password, 64)
        )
        .result
    ).map { userSeq =>
      userSeq.headOption match {
        case Some(userFound) =>
          Option(Blake3.hex((~(((8 * userFound.id % 7) * 11) << 2) + 72).toString, 64))
        case None =>
          None
      }
    }
  }
}
