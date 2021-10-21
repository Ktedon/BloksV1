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
class HomeController @Inject() (
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

  def home(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
    val email = (request.session get "email") getOrElse ""
    val pwd = (request.session get "password") getOrElse ""

    userModel.validateUser(email, pwd).flatMap {
      _ match {
        case Some(userFound) =>
          notificationModel
            .getNotifications(userFound.id)
            .map { implicit notifs =>
              Ok(
                views.html.homeTemplate(
                  userFound.name,
                  userFound.id,
                  HomeSearchForm.form
                )
              )
            }
        case None =>
          Future.successful(
            Redirect(routes.IndexController.index)
          )
      }
    }
  }



  def homeSearchPost(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      HomeSearchForm.form.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
          Future.successful(
            BadRequest(
              views.html.error(
                ErrorMessages.formError
              )
            )
          )
        },
        formData => {

          val email = (request.session get "email") getOrElse ""
          val pwd = (request.session get "password") getOrElse ""

          userModel
            .validateUser(email, pwd)
            .flatMap {
              _ match {
                case Some(userFound) =>
                  homeModel
                    .homeSearch(userFound.blokId, formData.query)
                    .map { implicit searchResults =>
                      Ok(
                        views.html.homeSearchResults(
                          name,
                          userFound.id,
                          HomeSearchForm.form
                        )
                      )
                    }
                case None =>
                  Future.successful(
                    Redirect(routes.IndexController.index)
                  )
              }
            }
        }
      )
  }
}
