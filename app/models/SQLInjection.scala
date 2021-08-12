package model

object SQLInjectionPrevention {

	def injectionCheck(queryList: String*): Boolean = {

		return queryList.foldLeft(false)(
			(left, right) =>
				left ||
				right.foldLeft(false)(
					(left, right) =>
						left || (right == '\'' || right == '"' || right == '\\')
					)
			)
	}
}
