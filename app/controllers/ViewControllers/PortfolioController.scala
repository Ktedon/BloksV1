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

@Singleton
class PortfolioController @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext)
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

  def portfolio(userId: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).flatMap {
                _.headOption match {
                  case Some(found) =>
                    userModel.validateUserByID(userId).map {
                      _ match {
                        case Some(profileUserFound) =>
                          Ok(
                            views.html.profile(profileUserFound.name)(
                              request,
                              models.PublicUser
                                .publicUserApply(profileUserFound)
                            )
                          )
                        case None =>
                          BadRequest("Something wen't wrong.")
                      }
                    }
                  case None =>
                    Future.successful(BadRequest("Something wen't wrong."))
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

  def poke(pokeId: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).map {
                _ match {
                  case Some(userFound) =>
                    notificationModel.pokeUser(
                      userFound.id,
                      userFound.name,
                      userFound.email,
                      userFound.profilePic,
                      pokeId
                    )
                    Redirect(routes.PortfolioController.portfolio(userFound.id))
                  case None => BadRequest("Something wen't wrong.")
                }
              }
            case None =>
              Future.successful(BadRequest("Something wen't wrong."))
          }
        case None =>
          Future.successful(BadRequest("Something wen't wrong."))
      }
  }

}
