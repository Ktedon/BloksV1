package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import model._
import scala.concurrent.Future

import play.api.libs.mailer._

@Singleton
class IndexController @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext, mailerClient: MailerClient)
    extends AbstractController(controllerComponents)
    with HasDatabaseConfigProvider[JdbcProfile]
    with play.api.i18n.I18nSupport {

  private val schoolModel       = new SchoolModel(db)(ec)
  private val userModel         = new UserModel(db)(ec)
  private val notificationModel = new NotificationModel(db)(ec)
  private val groupModel        = new GroupModel(db)(ec)
  private val messageModel      = new MessageModel(db)(ec)
  private val homeModel         = new HomeModel(db)(ec)
  private val settingModel      = new SettingModel(db)(ec)

  def sendEmail(key: String, emailInput: String) = {
    val cid = "1234"
    val email = Email(
      "Validate Bloks Account",
      "Bloks Validator <bloks@bloks.re>",
      Seq(s"${emailInput} <${emailInput}>"),
      // adds attachment
      attachments = Seq(
      ),
      // sends text, HTML or both...
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
    mailerClient.send(email)
  }

  def about() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.about())
  }

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index(LoginForm.form))
  }

  def indexPost() = Action.async { implicit request: Request[AnyContent] =>
    LoginForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(
          BadRequest(
            "I am sorry. But something went wrong. Try again, if that doesn't work then try again later."
          )
        )
      },
      formData => {
        userModel.validateUser(formData.email, formData.password)
          .map {
            _ match {
              case Some(foundUser) =>
                Redirect(routes.HomeController.home(foundUser.name))
                  .withSession(
                    "email" -> formData.email,
                    "password" -> formData.password
                  )
              case None =>
                BadRequest("Something went wrong. Try again later.")
            }
          }
      }
    )
  }

  def darnitIForgotMyPasscode() = Action {
    implicit request: Request[AnyContent] =>
      Ok("Too bad. You should have written it down.")
  }

  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.signUp(RegisterForm.form))
  }

  def registerPost() = Action.async { implicit request: Request[AnyContent] =>
    RegisterForm.form.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        Future.successful(
          BadRequest(
            "Something wen't wrong."
          )
        )
      },
      formData => {
        userModel.addUser(formData).map {
          if (_)
            Ok(
              "An email has been sent to your provided email address. There you will find the form to validate your account."
            )
          else
            BadRequest(
              "Something wen't wrong. Try filling out the form again. You may already have an account or our email service has failed."
            )
        }
      }
    )
  }

  def changeEmailValidation(key: String) = Action {
    implicit request: Request[AnyContent] =>
      Ok(views.html.validateEmail(key, LoginForm.form))
  }

  def changeEmailValidationPost(key: String) = Action.async {
    implicit request: Request[AnyContent] =>
      LoginForm.form.bindFromRequest.fold(
        formWithErrors => {
          Future.successful(
            BadRequest(
              "Something wen't wrong."
            )
          )
        },
        formData => {

          userModel
            .validateUserNoEmailVerification(formData.email, formData.password)
            .flatMap {
              _.headOption match {
                case Some(userFound) =>
                  if (models.helpers.AuthHelpers.testEmailValidationKey(userFound.id, key))
                    userModel
                      .modifyUserValidation(formData.email, formData.password)
                      .map { hasChanged =>
                        if (hasChanged)
                          Ok("Yasssss. You can now login")
                        else
                          BadRequest(
                            "Noooo. Something wen't wrong unexpectedly and it is totes my fault."
                          )
                      }

                  else
                    Future.successful(
                      BadRequest(
                        "Something is wrong."
                      )
                    )
                case None            =>
                  Future.successful(
                    BadRequest(
                    "Something is wrong."
                    )
                  )
              }
            }
        }
      )
  }

}
