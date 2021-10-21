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
class GroupController @Inject() (
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

  def groups(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val email = (request.session get "email") getOrElse ""
      val pwd = (request.session get "password") getOrElse ""

      userModel.validateUser(email, pwd).flatMap {
        _ match {
          case Some(userFound) =>
            groupModel.getGroups(userFound.blokId).map { implicit groups =>
              Ok(
                views.html.group(
                    name,
                    userFound.id,
                    GroupSearchForm.form,
                    GroupCreateForm.form
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

  def groupPost(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      GroupCreateForm.form.bindFromRequest.fold(
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
                  groupModel
                    .createGroup(
                      userFound.blokId,
                      formData.name,
                      formData.`type`.toShort
                    )
                    .map { bool =>
                      if (bool)
                        Redirect(routes.GroupController.groups(name))
                      else
                        BadRequest(
                          "Whoops. Something wen't wrong and by that I mean you wen't wrong"
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

  def group(name: String, groupId: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email = (request.session get "email") getOrElse ""
      val pwd = (request.session get "password") getOrElse ""

      userModel.validateUser(email, pwd).flatMap {
        _.headOption match {
          case Some(userFound) =>
            groupModel
              .getGroup(groupId)
              .flatMap { groups =>
                groups.headOption match {
                  case Some(groupFound) =>
                    groupModel.getThreadsAndUsers(groupId).map { implicit threadsAndUsers =>
                      Ok(views.html.groupTemplate(GroupSearchForm.form, ThreadCreateForm.form, userFound, groupFound))
                    }
                  case None             =>
                    Future.successful(
                      BadRequest("Something went wrong. Try again later.4")
                    )
                }
              }
          case None =>
            Future.successful(
              Redirect(routes.IndexController.index)
            )
        }
      }
  }

  def createThreadPost(name: String, groupName: String) =
    Action.async { implicit request: Request[AnyContent] =>
      ThreadCreateForm.form.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
          println("y")
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
                  groupModel.getGroups(userFound.blokId).flatMap {
                    _.filter(group =>
                      group.name == groupName
                    ).headOption match {
                      case Some(found) =>
                        groupModel
                          .addGroupThread(
                            found.id,
                            formData.parentID.toInt,
                            formData.title,
                            formData.text,
                            userFound.id
                          )
                          .flatMap { wasCreated =>
                            if (wasCreated)
                              Future.successful(
                                Redirect(
                                  routes.GroupController.group(name, found.id)
                                )
                              )
                            else
                              Future.successful(
                                BadRequest(
                                  views.html.error(
                                    "Something went wrong creating your post"
                                  )
                                )
                              )
                          }
                      case None =>
                        Future.successful(
                          BadRequest(
                            views.html.error(
                              "Something wen't"
                            )
                          )
                        )
                    }
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

    def groupSearchPost(name: String) = Action.async {
      implicit request: Request[AnyContent] =>
        GroupSearchForm.form.bindFromRequest.fold(
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
                _.headOption match {
                  case Some(userFound) =>
                    homeModel
                      .homeSearch(userFound.blokId, formData.query)
                      .map { implicit searchResults =>
                        Ok(
                          views.html
                            .groupSearchResults(
                              name,
                              userFound.id,
                              GroupSearchForm.form
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
