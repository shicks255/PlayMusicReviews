package controllers

import com.google.inject.{Inject, Singleton}
import models.{User, UserDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents, userDao: UserDao) extends AbstractController(cc) with I18nSupport{

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText(1),
      "password" -> nonEmptyText(1))
    ((userName, password) => User(userName.trim, password.trim))
    ((u: User) => Some(u.username, u.password))
  )

    def loginHome(msg: Option[String]) = Action { implicit request =>
      val message: String = msg match {
        case Some(x) => x
        case None => ""
      }
      Ok(views.html.login(loginForm)(message))
    }

    def login() = Action {implicit request: Request[AnyContent] =>
      loginForm.bindFromRequest().fold(
        errors => (BadRequest(views.html.login(errors)(""))),
        form => {
          val message: Option[Long] = userDao.loginUser(form, request)
          message match {
            case Some(x) => Redirect(routes.HomeController.index(Some("Successfully logged in")))
              .withSession(request.session + ("userId" -> x.toString))
            case _ => Redirect(routes.UserController.loginHome(Some("Invalid Login")))
          }
        }
      )
    }

  val registerForm = Form(
    mapping(
      "username" -> nonEmptyText(1),
      "password" -> nonEmptyText(1))
    ((userName, password) => User(userName.trim, password.trim))
    ((u: User) => Some(u.username, u.password)
    )
  )

  def registerHome(msg: Option[String]) = Action {implicit request =>
    val message: String = msg match {
      case Some(x) => x
      case None => ""
    }
    Ok(views.html.register(registerForm)(message))
  }

  def register = Action {implicit request =>
    registerForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.register(errors)(""))),
      form => {
        val result: Option[Long] = userDao.registerUser(form)
        result match {
          case Some(x) => Redirect(routes.HomeController.index(Some("Successfully Registered")))
          case _ => Redirect(routes.UserController.registerHome(Some("Invalid Username")))
        }
      }
    )
  }

  def userHome = Action{implicit request =>

    Ok(views.html.user())
  }

  def logout = Action{implicit request =>
    Ok(views.html.index("Logged out")).removingFromSession("userId").withNewSession
  }

}
