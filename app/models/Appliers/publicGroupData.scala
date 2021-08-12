package models

case class PublicGroup(id: Int, name: String, `type`: Short, icon: String)

object PublicGroup {
	@inline
	def publicGroupApply(groups: Seq[Tables.BlokGroupRow]): Seq[PublicGroup] = {

		var ret: Seq[PublicGroup] = Seq.empty

		for(group <- groups) {
			ret = ret :+ PublicGroup(group.id, group.name, group.`type`, group.icon)
		}
		return ret
	}
}
