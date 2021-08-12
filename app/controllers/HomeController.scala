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
import scala.util.{Failure, Success, Try}

import play.api.libs.mailer._
import java.io.File
import org.apache.commons.mail.EmailAttachment
import javax.inject.Inject

@Singleton
class HomeController @Inject() (
    protected val dbConfigProvider: DatabaseConfigProvider,
    controllerComponents: ControllerComponents
)(implicit ec: ExecutionContext, mailerClient: MailerClient)
    extends AbstractController(controllerComponents)
    with HasDatabaseConfigProvider[JdbcProfile]
    with play.api.i18n.I18nSupport {

  private val modelPersistent = new DatabaseModel(db)(ec)

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

  def index() = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.index(LoginForm.form))
  }

  def indexPost() = Action.async { implicit request: Request[AnyContent] =>
    LoginForm.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(
          Ok(
            "I am sorry. But something went wrong. Try again, if that doesn't work then try again later."
          )
        )
      },
      formData => {
        modelPersistent.modifyUserValidation(formData.email, formData.password)
        modelPersistent
          .validateUser(formData.email, formData.password)
          .flatMap { user =>
            user match {
              case Some(foundUser) =>
                modelPersistent
                  .getNotifications(formData.email, formData.password)
                  .map { notifs =>
                    Redirect(routes.HomeController.home(foundUser.name))
                      .withSession(
                        "email" -> formData.email,
                        "password" -> formData.password
                      )
                  }
              case None =>
                println("failure")
                Future.successful(Ok("Something went wrong. Try again later."))
            }
          }
      }
    )
  }

  def portfolio(userId: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              modelPersistent.validateUser(emailFound, passwordFound).flatMap {
                user =>
                  user.headOption match {
                    case Some(found) =>
                      modelPersistent.validateUserByID(userId).map { profileUser =>
                        profileUser match {
                          case Some(profileUserFound) =>
                            implicit val numFriends = 2
                            implicit val numFriended = 2
                            Ok(
                              views.html.profile(profileUserFound.name)(
                                request,
                                models.PublicUser.publicUserApply(profileUserFound),
                                numFriends,
                                numFriended
                              )
                            )
                          case None                   =>
                            BadRequest("Something wen't wrong.")
                        }

                      }
                    case None        =>
                      Future.successful(BadRequest("Something wen't wrong."))
                  }
              }

            case None =>
              Future.successful(BadRequest("Something went wrong. Try again later."))
          }

        case None =>
          Future.successful(BadRequest("Something went wrong. Try again later."))
      }

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
        modelPersistent.addUser(formData).flatMap { user =>
          user match {
            case Some(found) =>
                modelPersistent.getEmailValidationKey(formData.email, formData.password).map { keyOption =>
                  keyOption match {
                    case Some(keyFound) =>
                      sendEmail(keyFound, formData.email)
                      Ok(s"An email has been sent to ${formData.email}. Check your email to validate your account. If you can't find it check you spam folder or your all mail folder.")

                    case None           =>
                      BadRequest(
                        "Something went wrong. Try again later. You may already have an account."
                      )
                  }

                }
            case None =>
              Future.successful(BadRequest(
                "Something went wrong. Try again later. You may already have an account."
              ))
          }
        }
      }
    )
  }

  def home(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              modelPersistent.validateUser(emailFound, passwordFound).flatMap {
                user =>
                  user match {
                    case Some(foundUser) =>
                      modelPersistent
                        .getNotifications(emailFound, passwordFound)
                        .map { implicit notifs =>
                          implicit val form = HomeSearchForm.form
                          Ok(views.html.homeTemplate(foundUser.name, foundUser.id))
                        }
                    case None =>
                      Future.successful(
                        Ok("Something went wrong. Try again later.")
                      )
                  }
              }

            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }

        case None =>
          Future.successful(Ok("Something went wrong. Try again later."))
      }

  }

  def groups(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              modelPersistent.getGroups(emailFound, passwordFound).flatMap {
                implicit groups =>
                  modelPersistent.validateUser(emailFound, passwordFound).map { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        Ok(
                          views.html
                            .group(name,
                                userFound.id,
                                GroupSearchForm.form,
                                GroupCreateForm.form)
                        )
                      case None            =>
                        Ok("Something went wrong. Try again later.")
                    }

                  }
              }
            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }

        case None =>
          Future.successful(Ok("Something went wrong. Try again later."))
      }
  }

  def groupPost(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      GroupCreateForm.form.bindFromRequest.fold(
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
                  modelPersistent
                    .createGroup(
                      emailFound,
                      passwordFound,
                      formData.name,
                      formData.`type`.toShort
                    )
                    .map { bool =>
                      if (bool) Ok("Your group has been created. Yeah.")
                      else
                        Ok(
                          "Whoops. Something wen't wrong and by that I mean you wen't wrong"
                        )
                    }
                case None =>
                  Future.successful(
                    Ok("Something went wrong. Try again later.")
                  )
              }
            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }
        }
      )
  }

  def group(name: String, groupName: String) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              modelPersistent.validateUser(emailFound, passwordFound).flatMap {
                currentUserOption =>
                  currentUserOption.headOption match {
                    case Some(currentUser) =>
                      modelPersistent
                        .getGroups(emailFound, passwordFound)
                        .flatMap { group =>
                          models.PublicGroup
                            .publicGroupApply(
                              group.filter(group => group.name == groupName)
                            )
                            .headOption match {
                            case Some(found) =>
                              implicit val j = found
                              modelPersistent
                                .getGroupThreads(
                                  emailFound,
                                  passwordFound,
                                  found.id
                                )
                                .flatMap { implicit threads =>
                                  implicit var userSeq
                                      : Seq[models.Tables.BloksUserRow] =
                                    Seq.empty
                                  for (thread <- threads) {
                                    modelPersistent
                                      .validateUserByID(thread.userId)
                                      .map { user =>
                                        user match {
                                          case Some(userFound) =>
                                            if (userFound.id == thread.userId) {
                                              userSeq = userSeq :+ userFound
                                            }
                                        }
                                      }
                                  }
                                  if (threads.nonEmpty) {
                                    modelPersistent
                                      .validateUserByID(threads.last.userId)
                                      .map { user =>
                                        user match {
                                          case Some(userFound) =>
                                            if (
                                              userFound.id == threads.last.userId
                                            ) {
                                              userSeq = userSeq :+ userFound
                                            }
                                        }
                                      }
                                  }
                                  implicit var threadUserSeq =
                                    threads zip userSeq
                                  Future.successful(
                                    Ok(
                                      views.html.groupTemplate(
                                        name,
                                        groupName,
                                        GroupSearchForm.form,
                                        ThreadCreateForm.form,
                                        models.PublicUser
                                          .publicUserApply(currentUser)
                                      )
                                    )
                                  )
                                }
                            case None =>
                              Future.successful(
                                BadRequest(
                                  "Something went wrong. Try again later."
                                )
                              )
                          }
                        }
                    case None =>
                      Future.successful(
                        BadRequest("Something went wrong. Try again later.")
                      )
                  }
              }
            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }
        case None =>
          Future.successful(Ok("Something went wrong. Try again later."))
      }
  }

  def createThreadPost(name: String, groupName: String, extendsID: Int) =
    Action.async { implicit request: Request[AnyContent] =>
      ThreadCreateForm.form.bindFromRequest.fold(
        formWithErrors => {
          // binding failure, you retrieve the form containing errors:
          println("y")
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
                  modelPersistent.getGroups(emailFound, passwordFound).flatMap {
                    groups =>
                      // Future.successful(Ok("Yeah"))
                      groups
                        .filter(group => group.name == groupName)
                        .headOption match {
                        case Some(found) =>
                          modelPersistent
                            .addGroupThread(
                              emailFound,
                              passwordFound,
                              found.id,
                              formData.parentID.toInt,
                              formData.title,
                              formData.text
                            )
                            .flatMap { addCount =>
                              if (addCount) {
                                Future.successful(
                                  Redirect(
                                    routes.HomeController.group(name, groupName)
                                  )
                                )
                              } else {
                                Future.successful(Ok("Nah"))
                              }
                            }

                        case None => Future.successful(Ok("Bummer"))
                      }

                  }
                case None =>
                  Future.successful(
                    Ok("Something went wrong. Try again later.")
                  )
              }
            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }
        }
      )
    }

  def FourZeroFour() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.fourZeroFour())
  }

  def about() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.about())
  }

  def homeSearchPost(name: String) = Action.async {
    implicit request: Request[AnyContent] =>
      HomeSearchForm.form.bindFromRequest.fold(
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
                  modelPersistent
                    .homeSearch(emailFound, passwordFound, formData.query)
                    .flatMap { implicit searchResults =>
                      modelPersistent.validateUser(emailFound, passwordFound).map { userOption =>
                        userOption match {
                          case Some(userFound) =>
                            Ok(
                              views.html.homeSearchResults(name, userFound.id, HomeSearchForm.form)
                            )

                          case None            =>
                              BadRequest("Something went wrong. Try again later.")
                        }
                      }
                    }

                case None =>
                  Future.successful(
                    BadRequest("Something went wrong. Try again later.")
                  )
              }
            case None =>
              Future.successful(BadRequest("Something went wrong. Try again later."))
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
                  modelPersistent
                    .homeSearch(emailFound, passwordFound, formData.query)
                    .flatMap { implicit searchResults =>
                      modelPersistent.validateUser(emailFound, passwordFound).map { userSeq =>
                        userSeq.headOption match {
                          case Some(userFound) =>
                            Ok(
                              views.html
                                .groupSearchResults(name, userFound.id, GroupSearchForm.form)
                            )
                          case None            =>
                            BadRequest("Something went wrong. Try again later.")
                        }


                      }
                    }

                case None =>
                  Future.successful(
                    BadRequest("Something went wrong. Try again later.")
                  )
              }
            case None =>
              Future.successful(BadRequest("Something went wrong. Try again later."))
          }
        }
      )
  }

  def message(yourName: String, toID: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              modelPersistent.validateUser(emailFound, passwordFound).flatMap {
                userOption =>
                  userOption match {
                    case Some(user) =>
                      modelPersistent.getMessages(user.id, toID).flatMap {
                        implicit messages =>
                          modelPersistent.validateUserByID(toID).map {
                            toUserOption =>
                              toUserOption match {
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
                                  Ok("Something wen't wrong.")
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

          // println()

          val email: Option[String] = request.session.get("email")
          val password: Option[String] = request.session.get("password")

          email match {
            case Some(emailFound) =>
              password match {
                case Some(passwordFound) =>
                  modelPersistent
                    .validateUser(emailFound, passwordFound)
                    .flatMap { userOption =>
                      userOption match {
                        case Some(userFound) =>
                          modelPersistent
                            .addMessage(
                              userFound.id,
                              formData.toID,
                              formData.message
                            )
                            .map { addCount =>
                              if (addCount) {
                                Redirect(
                                  routes.HomeController
                                    .message(userFound.name, formData.toID)
                                )
                              } else {
                                Ok("Something wen't wrong.")
                              }
                            }

                        case None =>
                          Future.successful(Ok("Something went wrong"))
                      }

                    }

                case None =>
                  Future.successful(
                    Ok("Something went wrong. Try again later.")
                  )
              }
            case None =>
              Future.successful(Ok("Something went wrong. Try again later."))
          }
        }
      )
  }

  def poke(pokeId: Int) = Action.async {
    implicit request: Request[AnyContent] =>
      val email: Option[String] = request.session.get("email")
      val password: Option[String] = request.session.get("password")

      email match {
        case Some(emailFound) =>
          password match {
            case Some(passwordFound) =>
              modelPersistent.validateUser(emailFound, passwordFound).map { userOption =>
                userOption match {
                  case Some(userFound) =>
                    modelPersistent.pokeUser(userFound.id, userFound.name, userFound.email, userFound.profilePic, pokeId)

                    Redirect(routes.HomeController.portfolio(userFound.id))

                  case None            => BadRequest("Something wen't wrong.")
                }

              }
            case None                =>
              Future.successful(BadRequest("Something wen't wrong."))
          }
        case None             =>
          Future.successful(BadRequest("Something wen't wrong."))
      }
  }

  def settings(name: String) = Action.async { implicit request: Request[AnyContent] =>
    val email: Option[String] = request.session.get("email")
    val password: Option[String] = request.session.get("password")

    email match {
      case Some(emailFound) =>
        password match {
          case Some(passwordFound) =>
            modelPersistent.validateUser(emailFound, passwordFound).map { userSeq =>
              userSeq.headOption match {
                case Some(userFound) =>
                  Ok(views.html.settings(name, userFound.id))
                case None            => BadRequest("Something wen't Wrong")
              }
            }
          case None                => Future.successful(BadRequest("Something wen't Wrong"))
        }
      case None             => Future.successful(BadRequest("Something wen't Wrong"))
    }
  }

  def changeIconPost() = Action.async {
    implicit request: Request[AnyContent] =>
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changeIcon(userFound.id, s"https://avatars.dicebear.com/api/${formData.`type`}/${formData.seed}.svg").map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest(
                              "Something wen't wrong."
                            )
                          }

                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None                => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changePassword(userFound.id, formData.newpwd).map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest("Something wen't Wrong")
                          }
                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None             => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
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
              modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                userSeq.headOption match {
                  case Some(userFound) =>
                    modelPersistent.deleteAccount(userFound.id).map { hasDeleted =>
                      if (hasDeleted) {
                        Ok(views.html.settings("name", userFound.id))
                      } else {
                        BadRequest("Something wen't Wrong")
                      }
                    }
                  case None            => Future.successful(BadRequest("Something wen't Wrong"))
                }
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None             => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeGenderPost = Action.async {
    implicit request: Request[AnyContent] =>
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changeGender(userFound.id, formData.newGender).map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest("Something wen't Wrong")
                          }
                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None             => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
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
              modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                userSeq.headOption match {
                  case Some(userFound) =>
                    modelPersistent.changeGenderVisibility(userFound.id, !userFound.showGender).map { hasChanged =>
                      if (hasChanged) {
                        Ok(views.html.settings("name", userFound.id))
                      } else {
                        BadRequest("Something wen't Wrong")
                      }
                    }
                  case None            => Future.successful(BadRequest("Something wen't Wrong"))
                }
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None             => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeSexPost = Action.async {
    implicit request: Request[AnyContent] =>
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changeSex(userFound.id, formData.newSex).map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest("Something wen't Wrong")
                          }
                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None             => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
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
              modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                userSeq.headOption match {
                  case Some(userFound) =>
                    modelPersistent.changeSexVisibility(userFound.id, !userFound.showBiologicalSex).map { hasChanged =>
                      if (hasChanged) {
                        Ok(views.html.settings("name", userFound.id))
                      } else {
                        BadRequest("Something wen't Wrong")
                      }
                    }
                  case None            => Future.successful(BadRequest("Something wen't Wrong"))
                }
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
          }
        case None             => Future.successful(BadRequest("Something wen't Wrong"))
      }
  }

  def changeGradePost = Action.async {
    implicit request: Request[AnyContent] =>
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changeGrade(userFound.id, formData.newGrade.toInt).map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest("Something wen't Wrong")
                          }
                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None             => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changeRelStatus(userFound.id, formData.newStatus).map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest("Something wen't Wrong")
                          }
                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None             => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
          }
        }
      )
  }

  def changeBioPost = Action.async {
    implicit request: Request[AnyContent] =>
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
                  modelPersistent.validateUser(emailFound, passwordFound).flatMap { userSeq =>
                    userSeq.headOption match {
                      case Some(userFound) =>
                        modelPersistent.changeBiography(userFound.id, formData.newBio).map { hasChanged =>
                          if (hasChanged) {
                            Ok(views.html.settings("name", userFound.id))
                          } else {
                            BadRequest("Something wen't Wrong")
                          }
                        }
                      case None            => Future.successful(BadRequest("Something wen't Wrong"))
                    }
                  }
                case None             => Future.successful(BadRequest("Something wen't Wrong"))
              }
            case None             => Future.successful(BadRequest("Something wen't Wrong"))
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
          modelPersistent.testEmailValidationKey(formData.email, formData.password, key).flatMap { keymatch =>
            if (keymatch) {
              modelPersistent.modifyUserValidation(formData.email, formData.password).map { hasChanged =>
                if (hasChanged) {
                  Ok("Yasssss. You can now login")
                } else {
                  BadRequest("Noooo. Something wen't wrong unexpectedly and it is totes my fault.")
                }
              }
            } else {
              Future.successful(BadRequest(
                "Something is wrong."
              ))
            }
          }
        }
      )
  }

}
