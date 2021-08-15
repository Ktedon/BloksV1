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
class SettingController @Inject() (
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

  def settings(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).map {
                _.headOption match {
                  case Some(userFound) =>
                    Ok(views.html.settings(name, userFound.id))
                  case None => BadRequest("Something wen't Wrong")
                }
              }
            case None => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeIconPost() = Action.async { implicit request: Request[AnyContent] =>
    ChangeIconForm.form.bindFromRequest.fold(
      formWithErrors => {
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
                    _.headOption match {
                      case Some(userFound) =>
                        settingModel
                          .changeIcon(
                            userFound.id,
                            s"https://avatars.dicebear.com/api/${formData.`type`}/${formData.seed}.svg"
                          )
                          .map { hasChanged =>
                            if (hasChanged)
                              Ok(views.html.settings("name", userFound.id))
                            else
                              BadRequest(
                                "Something wen't wrong."
                              )
                          }
                      case None =>
                        Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
              case None =>
                Future.successful(BadRequest("Something wen't Wrong"))
            }
          case None => Future.successful(BadRequest("Something wen't Wrong"))
        }
      }
    )
  }

  def changePasswordPost = Action.async {
    implicit request: Request[AnyContent] =>
      ChangePasswordForm.form.bindFromRequest.fold(
        formWithErrors => {
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
                      _.headOption match {
                        case Some(userFound) =>
                          settingModel
                            .changePassword(userFound.id, formData.newpwd)
                            .map { hasChanged =>
                              if (hasChanged)
                                Ok(views.html.settings("name", userFound.id))
                              else
                                BadRequest("Something wen't Wrong")
                            }
                        case None =>
                          Future.successful(BadRequest("Something wen't Wrong"))
                      }
                    }
                case None =>
                  Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None => Future.successful(BadRequest("Something wen't Wrong"))
          }
        }
      )
  }

  def deleteAccountPost = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).flatMap {
                _.headOption match {
                  case Some(userFound) =>
                    settingModel.deleteAccount(userFound.id).map {
                      hasDeleted =>
                        if (hasDeleted)
                          Ok(views.html.settings("name", userFound.id))
                        else
                          BadRequest("Something wen't Wrong")
                    }
                  case None =>
                    Future.successful(BadRequest("Something wen't Wrong"))
                }
              }
            case None => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeGenderPost = Action.async { implicit request: Request[AnyContent] =>
    ChangeGenderForm.form.bindFromRequest.fold(
      formWithErrors => {
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
                    _.headOption match {
                      case Some(userFound) =>
                        settingModel
                          .changeGender(userFound.id, formData.newGender)
                          .map { hasChanged =>
                            if (hasChanged)
                              Ok(views.html.settings("name", userFound.id))
                            else
                              BadRequest("Something wen't Wrong")
                          }
                      case None =>
                        Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
              case None =>
                Future.successful(BadRequest("Something wen't Wrong"))
            }
          case None => Future.successful(BadRequest("Something wen't Wrong"))
        }
      }
    )
  }

  def changeGenderVisibilityPost = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).flatMap {
                _.headOption match {
                  case Some(userFound) =>
                    settingModel
                      .changeGenderVisibility(
                        userFound.id,
                        !userFound.showGender
                      )
                      .map { hasChanged =>
                        if (hasChanged)
                          Ok(views.html.settings("name", userFound.id))
                        else
                          BadRequest("Something wen't Wrong")
                      }
                  case None =>
                    Future.successful(BadRequest("Something wen't Wrong"))
                }
              }
            case None => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeSexPost = Action.async { implicit request: Request[AnyContent] =>
    ChangeSexForm.form.bindFromRequest.fold(
      formWithErrors => {
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
                    _.headOption match {
                      case Some(userFound) =>
                        settingModel
                          .changeSex(userFound.id, formData.newSex)
                          .map { hasChanged =>
                            if (hasChanged)
                              Ok(views.html.settings("name", userFound.id))
                            else
                              BadRequest("Something wen't Wrong")
                          }
                      case None =>
                        Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
              case None =>
                Future.successful(BadRequest("Something wen't Wrong"))
            }
          case None => Future.successful(BadRequest("Something wen't Wrong"))
        }
      }
    )
  }

  def changeSexVisibilityPost = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              userModel.validateUser(emailFound, passwordFound).flatMap {
                _.headOption match {
                  case Some(userFound) =>
                    settingModel
                      .changeSexVisibility(
                        userFound.id,
                        !userFound.showBiologicalSex
                      )
                      .map { hasChanged =>
                        if (hasChanged)
                          Ok(views.html.settings("name", userFound.id))
                        else
                          BadRequest("Something wen't Wrong")
                      }
                  case None =>
                    Future.successful(BadRequest("Something wen't Wrong"))
                }
              }
            case None => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeGradePost = Action.async { implicit request: Request[AnyContent] =>
    ChangeGradeForm.form.bindFromRequest.fold(
      formWithErrors => {
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
                    _.headOption match {
                      case Some(userFound) =>
                        settingModel
                          .changeGrade(userFound.id, formData.newGrade.toInt)
                          .map { hasChanged =>
                            if (hasChanged)
                              Ok(views.html.settings("name", userFound.id))
                            else
                              BadRequest("Something wen't Wrong")
                          }
                      case None =>
                        Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
              case None =>
                Future.successful(BadRequest("Something wen't Wrong"))
            }
          case None => Future.successful(BadRequest("Something wen't Wrong"))
        }
      }
    )
  }

  def changeRelStatusPost = Action.async {
    implicit request: Request[AnyContent] =>
      ChangeRelStatusForm.form.bindFromRequest.fold(
        formWithErrors => {
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
                      _.headOption match {
                        case Some(userFound) =>
                          settingModel
                            .changeRelStatus(userFound.id, formData.newStatus)
                            .map { hasChanged =>
                              if (hasChanged)
                                Ok(views.html.settings("name", userFound.id))
                              else
                                BadRequest("Something wen't Wrong")
                            }
                        case None =>
                          Future.successful(BadRequest("Something wen't Wrong"))
                      }
                    }
                case None =>
                  Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None => Future.successful(BadRequest("Something wen't Wrong"))
          }
        }
      )
  }

  def changeBioPost = Action.async { implicit request: Request[AnyContent] =>
    ChangeBiographyForm.form.bindFromRequest.fold(
      formWithErrors => {
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
                    _.headOption match {
                      case Some(userFound) =>
                        settingModel
                          .changeBiography(userFound.id, formData.newBio)
                          .map { hasChanged =>
                            if (hasChanged)
                              Ok(views.html.settings("name", userFound.id))
                            else
                              BadRequest("Something wen't Wrong")
                          }
                      case None =>
                        Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
              case None =>
                Future.successful(BadRequest("Something wen't Wrong"))
            }
          case None => Future.successful(BadRequest("Something wen't Wrong"))
        }
      }
    )
  }
}
