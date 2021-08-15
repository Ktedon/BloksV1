package models.helpers

object EmailHelpers {
  import play.api.libs.mailer._

  /*
    This will send the email for validating the user. It is always from the
    email bloks@bloks.re.
  */
  def sendEmail(key: String, emailInput: String)(implicit
      mailer: play.api.libs.mailer.MailerClient
  ) = {
    val cid = "1234"
    val email = Email(
      "Validate Bloks Account",
      "Bloks Validator <bloks@bloks.re>",
      Seq(s"${emailInput} <${emailInput}>"),
      attachments = Seq.empty, 
      bodyText = Some("Go here for your validation form."),
      bodyHtml = Some(s"""<html>
        <body>
          <div style="border-radius: 10px; width: 70%; margin-left: 15%; margin-top: 80px; background-color: lightgray;">
            <br>
            <h3 style="text-align: center; margin-top: 20px">Click below to find see the validation form.</h3><br><br>
            <a href="bloks.re/emailValidateUser/${key}">
              <button style="display: block; margin: 0 auto; height: 30px; background-color: #4CAF50; border: none; color: white; text-align: center; text-decoration: none; display: block; font-size: 16px;">Click Here!</button>
            </a>
            <br>
            <h3 style="text-align: center; margin-top: 20px">If that does not work go here:</h3><br>
            <div style="text-align: center;">bloks.re/emailValidateUser/${key}</div>
            <br>
          </div>
        </body>
        </html>""")
    )
    mailer.send(email)
  }

  /*
    Retrieves the email extension for a given email.
  */
  def getEmailExtension(email: String): String = {
    var extension = ""
    var counter = email.length() - 1

    while (email(counter) != '@') {
      extension += email(counter)
      counter -= 1
    }
    return extension.reverse
  }
}
