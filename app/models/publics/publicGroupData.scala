package models

case class PublicGroup(id: Int, name: String, `type`: Short, icon: String)

object PublicGroup {
  def publicGroupApply(groups: Seq[Tables.BlokGroupRow]): Seq[PublicGroup] =
    groups.foldLeft(Seq[PublicGroup]()) { (publicGroups, group) =>
      publicGroups :+ PublicGroup(
        group.id,
        group.name,
        group.`type`,
        group.icon
      )
    }

  def publicGroupApply(groups: Tables.BlokGroupRow): PublicGroup =
    PublicGroup(
      groups.id,
      groups.name,
      groups.`type`,
      groups.icon
    )
}
