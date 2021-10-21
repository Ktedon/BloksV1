package controllers

object ErrorMessages {

  @inline
  val formError = {
    """
      At least one of the parameters you provided was invalid. Try
      double checking to make sure all the forms appear green. If
      that doesn't work then try again later.
    """
  }

  @inline
  val accountCreationError = {
    """
      We failed to create your account. Please
      try again later.
    """
  }
}

object SuccessMessages {
  @inline
  val accountCreationSuccess = {
    """
      You're account has now been validated and you
      can log in.
    """
  }
}
