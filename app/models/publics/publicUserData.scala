package models

case class PublicUser(
    id: Int,
    blokID: Int,
    name: String,
    electedRank: Short,
    grade: Int,
    relationshipStatus: String,
    gender: Option[String],
    biologicalSex: Option[String],
    biography: String,
    profilePic: String
)

object PublicUser {
  def publicUserApply(users: Seq[Tables.BloksUserRow]): Seq[PublicUser] = {
    if (users.isEmpty)
      Seq.empty
    else if (users.head.showGender && users.head.showBiologicalSex)
      users.foldLeft(Seq[PublicUser]()) { (publicUsers, user) =>
        publicUsers :+ PublicUser(
          user.id,
          user.blokId,
          user.name,
          user.electedRank,
          user.grade,
          user.relationshipStatus,
          Option(user.gender),
          Option(user.biologicalSex),
          user.biography,
          user.profilePic
        )
      }
    else if (users.head.showGender)
      users.foldLeft(Seq[PublicUser]()) { (publicUsers, user) =>
        publicUsers :+ PublicUser(
          user.id,
          user.blokId,
          user.name,
          user.electedRank,
          user.grade,
          user.relationshipStatus,
          Option(user.gender),
          None,
          user.biography,
          user.profilePic
        )
      }
    else if (users.head.showBiologicalSex)
      users.foldLeft(Seq[PublicUser]()) { (publicUsers, user) =>
        publicUsers :+ PublicUser(
          user.id,
          user.blokId,
          user.name,
          user.electedRank,
          user.grade,
          user.relationshipStatus,
          None,
          Option(user.biologicalSex),
          user.biography,
          user.profilePic
        )
      }
    else
      users.foldLeft(Seq[PublicUser]()) { (publicUsers, user) =>
        publicUsers :+ PublicUser(
          user.id,
          user.blokId,
          user.name,
          user.electedRank,
          user.grade,
          user.relationshipStatus,
          None,
          None,
          user.biography,
          user.profilePic
        )
      }
  }

  def publicUserApply(user: Tables.BloksUserRow): PublicUser = {
    if (user.showGender && user.showBiologicalSex)
      PublicUser(
        user.id,
        user.blokId,
        user.name,
        user.electedRank,
        user.grade,
        user.relationshipStatus,
        Option(user.gender),
        Option(user.biologicalSex),
        user.biography,
        user.profilePic
      )
    else if (user.showGender)
      PublicUser(
        user.id,
        user.blokId,
        user.name,
        user.electedRank,
        user.grade,
        user.relationshipStatus,
        Option(user.gender),
        None,
        user.biography,
        user.profilePic
      )
    else if (user.showBiologicalSex)
      PublicUser(
        user.id,
        user.blokId,
        user.name,
        user.electedRank,
        user.grade,
        user.relationshipStatus,
        None,
        Option(user.biologicalSex),
        user.biography,
        user.profilePic
      )
    else
      PublicUser(
        user.id,
        user.blokId,
        user.name,
        user.electedRank,
        user.grade,
        user.relationshipStatus,
        None,
        None,
        user.biography,
        user.profilePic
      )
  }
}
