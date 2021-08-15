package models.helpers

object AuthHelpers {
  import ky.korins.blake3.Blake3

  def testEmailValidationKey(userId: Int, key: String): Boolean =
    Blake3.hex((~(((userId) * 11) << 2) + 72).toString, 64) == key

  def getEmailValidationKey(userId: Int): String =
    Blake3.hex((~(((userId) * 11) << 2) + 72).toString, 64)
}
