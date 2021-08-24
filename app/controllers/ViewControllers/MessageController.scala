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

import play.api.libs.streams.ActorFlow
import akka.actor.ActorSystem
import akka.stream.Materializer

@Singleton
class MessageController @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext, system: ActorSystem, mat: Materializer)
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

  def message(yourName: String, toID: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).flatMap {
                _ match {
                  case Some(user) =>
                    messageModel.getMessages(user.id, toID).flatMap {
                      implicit messages =>
                        userModel.validateUserByID(toID).map {
                          _ match {
                            case Some(toUser) =>
                              Ok(
                                views.html.message(
                                  yourName,
                                  CreateMessageForm.form,
                                  user.id,
                                  toID,
                                  toUser,
                                  user
                                )
                              )
                            case None =>
                              BadRequest("Something wen't wrong.")
                          }
                        }
                    }
                  case None =>
                    Future.successful(BadRequest("Something wen't wrong."))
                }
              }
            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }
        case None =>
          Future.successful(Ok("Something went wrong. Try again later."))
      }
  }

  def createMessagePost() = Action.async {
    implicit request: Request[AnyContent] =>
      CreateMessageForm.form.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
          Future.successful(
            BadRequest(
              "Something wen't wrong."
            )
          )
        },
        formData => {

          val email: Option[String] = request.session.get("email")
          val password: Option[String] = request.session.get("password")

          email match {
            case Some(emailFound) =>
              password match {
                case Some(passwordFound) =>
                  userModel
                    .validateUser(emailFound, passwordFound)
                    .flatMap {
                      _ match {
                        case Some(userFound) =>
                          messageModel
                            .addMessage(
                              userFound.id,
                              formData.toID,
                              formData.message
                            )
                            .map { addCount =>
                              if (addCount)
                                Redirect(
                                  routes.MessageController
                                    .message(userFound.name, formData.toID)
                                )
                              else
                                BadRequest("Something wen't wrong.")
                            }
                        case None =>
                          Future.successful(BadRequest("Something went wrong"))
                      }
                    }
                case None =>
                  Future.successful(
                    BadRequest("Something went wrong. Try again later.")
                  )
              }
            case None =>
              Future.successful(
                BadRequest("Something went wrong. Try again later.")
              )
          }
        }
      )
  }

  def messageSocket = WebSocket.acceptOrResult[String, String] { request =>
    val email: Option[String] = request.session.get("email")
    val password: Option[String] = request.session.get("password")

    if (email.isInstanceOf[Some[String]] && password.isInstanceOf[Some[String]])
      userModel.validateUser(email.get, password.get).map { userOption =>
        if (userOption.isInstanceOf[Some[models.Tables.BloksUserRow]])
          Right(ActorFlow.actorRef { out =>
            ChatActor.props(out, messageModel)
          })
        else
          Left(Forbidden)
      }
    else
      Future.successful(Left(Forbidden))
  }

  def contacts(name: String) = Action.async {
    implicit request: Request[AnyContent] =>

    val email: Option[String] = request.session.get("email")
    val password: Option[String] = request.session.get("password")

    if (email.isInstanceOf[Some[String]] && password.isInstanceOf[Some[String]])
      userModel.validateUser(email.get, password.get).flatMap {
        _ match {
          case Some(userFound) =>
            messageModel.getContacts(userFound.id).map { implicit contacts =>
              implicit val thing = HomeSearchForm.form
              Ok(views.html.contacts(models.PublicUser.publicUserApply(userFound)))
            }
          case None            =>
            Future.successful(
              BadRequest("Something went wrong. Try again later.")
            )
        }
      }
    else
      Future.successful(BadRequest("Something went wrong. Try again later."))
  }
}
