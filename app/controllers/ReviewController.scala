package controllers

import com.google.inject.Singleton
import javax.inject.Inject
import models.{Review, ReviewDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class ReviewController @Inject()(cc: ControllerComponents, dao: ReviewDao) extends AbstractController(cc) with I18nSupport{

  val reviewForm = Form(
    mapping(
      "artist" -> nonEmptyText(1))
    ((name) => Review(None, name, "", "", ""))
    ((r: Review) => Some((r.artist)))
  )

  def addReviewHome = Action { implicit request =>
    Ok(views.html.addReview(reviewForm))
  }

  def addReview = Action {implicit request: Request[AnyContent] =>
    println("first step")
    reviewForm.bindFromRequest.fold(
      errors => {BadRequest(views.html.addReview(errors))},
      form => {
        val result: Option[Long] = dao.saveReview(form)
        result match {
          case Some(x) => Redirect(routes.HomeController.index(Some("Review successfully added")))
          case _ => Redirect(routes.ReviewController.addReviewHome())
        }
      }
    )
  }
}
