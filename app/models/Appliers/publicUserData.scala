package models

case class PublicUser(id: Int, blokID: Int, name: String, electedRank: Short, grade: Int,
	relationshipStatus: String, gender: Option[String], biologicalSex: Option[String],
	biography: String, profilePic: String)

object PublicUser {

	def publicUserApply(users: Seq[Tables.BloksUserRow]): Seq[PublicUser] = {

		var ret: Seq[PublicUser] = Seq.empty

		for(user <- users) {
			if (user.showGender && user.showBiologicalSex) {
				ret = ret :+ PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
					user.relationshipStatus, Option(user.gender), Option(user.biologicalSex),
					user.biography, user.profilePic)

			} else if (user.showGender) {
				ret = ret :+ PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
					user.relationshipStatus, Option(user.gender), None, user.biography, user.profilePic)

			} else if (user.showBiologicalSex) {
				ret = ret :+ PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
					user.relationshipStatus, None, Option(user.biologicalSex), user.biography, user.profilePic)

			} else {
				ret = ret :+ PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
					user.relationshipStatus, None, None, user.biography, user.profilePic)
			}
		}

		return ret
	}

	def publicUserApply(user: Tables.BloksUserRow): PublicUser = {

		if (user.showGender && user.showBiologicalSex) {
			return PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
				user.relationshipStatus, Option(user.gender), Option(user.biologicalSex),
				user.biography, user.profilePic)

		} else if (user.showGender) {
			return PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
				user.relationshipStatus, Option(user.gender), None, user.biography, user.profilePic)

		} else if (user.showBiologicalSex) {
			return PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
				user.relationshipStatus, None, Option(user.biologicalSex), user.biography, user.profilePic)

		} else {
			return PublicUser(user.id, user.blokId, user.name, user.electedRank, user.grade,
				user.relationshipStatus, None, None, user.biography, user.profilePic)
		}

	}
}
