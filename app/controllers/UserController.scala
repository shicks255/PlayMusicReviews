package controllers

import com.google.inject.{Inject, Singleton}
import models.album.AlbumDao
import models.review.{Review, ReviewDao}
import models.user.{EditUserForm, User, UserDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class UserController @Inject()(cc: ControllerComponents, userDao: UserDao, reviewDao: ReviewDao, albumDao: AlbumDao) extends AbstractController(cc) with I18nSupport{

  val loginForm = Form(
    mapping(
      "Username" -> nonEmptyText,
      "Password" -> nonEmptyText)
    ((userName, password) => User(userName.trim, password.trim, 0, None, false, false))
    ((u: User) => Some(u.username, u.password))
  )

  val editForm = Form(
    mapping(
      "userId" -> longNumber,
      "email" -> text,
      "emailList" -> boolean)
    ((id, email, emailList) => EditUserForm(id, email, emailList))
    ((form: EditUserForm) => Some(form.userId, form.email, form.emailList))
  )

  def editUser(id: Long) = Action{implicit request =>
    editForm.bindFromRequest().fold(
      errors => {
        Redirect(routes.UserController.userHome())
      },
      form => {
        userDao.updateUser(form)
        Redirect(routes.UserController.userHome())
      }
    )
  }

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
    ((userName, password) => User(userName.trim, password.trim, 0, None, false, false))
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
    val userId: Option[String] = request.session.get("userId") match {
      case Some(x) => Some(x)
      case None => None
    }

    if (userId.nonEmpty)
    {
      val user: User = userDao.getUserFromId(userId.get.toLong)
      val reviews: List[Review] = reviewDao.getUserReviews(userId.get.toLong)
      val fullReviews = for {
        r <- reviews
        a <- albumDao.getAlbum(r.albumId)
        fa <- albumDao.getFullAlbum(a)
        fr <- reviewDao.getFullReview(r, fa)
      } yield fr

      val editUserForm = EditUserForm(user.id, user.emailAddress.getOrElse(""), user.emailList)
      val userStats = reviewDao.getUserStats(user.id)
      Ok(views.html.userAccount(fullReviews, editForm.fill(editUserForm), user, true, userStats))
    }
    else
      Redirect(routes.UserController.login())
  }

  def userHome2(id: Long) = Action{ implicit request =>
    val reviews: List[Review] = reviewDao.getUserReviews(id)
    val user: User = userDao.getUserFromId(id)

    val fullReviews = for {
      r <- reviews
      a <- albumDao.getAlbum(r.albumId)
      fa <- albumDao.getFullAlbum(a)
      fr <- reviewDao.getFullReview(r, fa)
    } yield fr

    val userStats = reviewDao.getUserStats(id)

    Ok(views.html.userAccount(fullReviews, editForm, user, false, userStats))
  }

  def logout = Action{implicit request =>
    Redirect(routes.HomeController.index(Some("Logged Out"))).removingFromSession("userId")
  }

}
