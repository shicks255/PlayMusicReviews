package controllers

import com.google.inject.{Inject, Singleton}
import models.{User, UserDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents, dao: UserDao) extends AbstractController(cc) with I18nSupport{

  val loginForm = Form(
    mapping(
      "" -> nonEmptyText(1),
      "" -> nonEmptyText(1))
    ((userName, password) => User.getUser(userName, password))
    ((u: User) => Some(u.username, u.password))
  )

  //  def loginHome() = Action { implicit request =>
  //    Ok(views.html.login(loginForm))
  //  }
  //
  //  def login() = Action {implicit request: Request[AnyContent] =>
  //    loginForm.bindFromRequest().fold(
  //      errors => (BadRequest(views.html.login(errors))),
  //      form => {
  //        val message: String = User.login()
  //        Redirect(routes.HomeController.index(Some("Sucessfully Logged In")))
  //      }
  //    )
  //  }

  val registerForm = Form(
    mapping(
      "username" -> nonEmptyText(1),
      "password" -> nonEmptyText(1))
    ((userName, password) => User(userName, password))
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
        val result: Option[Long] = dao.registerUser(form)
        result match {
          case Some(x) => Redirect(routes.HomeController.index(Some("Successfully Registered")))
          case _ => Redirect(routes.UserController.registerHome(Some("Invalid Username")))
        }
      }
    )
  }

}
