package controllers

import com.google.inject.Singleton
import javax.inject.Inject
import models.Review
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class ReviewController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport{

  val reviewForm = Form(
    mapping(
      "artist" -> nonEmptyText(1))
    ((name) => Review(name, "", "", ""))
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
        val message: String = Review.saveReview(form)
        Redirect(routes.HomeController.index(Some(message)))
      }
    )
  }
}
