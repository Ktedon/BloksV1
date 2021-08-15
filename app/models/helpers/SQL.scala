package model

object SQL {
  def injectionCheck(queryList: String*): Boolean = {
    queryList.foldLeft(false)((left, right) =>
      left ||
        right.foldLeft(false)((left, right) =>
          left || (right == '\\')
        )
    )
  }
}
