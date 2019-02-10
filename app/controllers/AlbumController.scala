package controllers

import java.time.LocalDateTime

import com.google.inject.{Inject, Singleton}
import models.{Album, AlbumDao, Review, ReviewDao}
import play.api.data.Forms.{longNumber, mapping}
import play.api.i18n.I18nSupport
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

@Singleton
class AlbumController @Inject() (cc: ControllerComponents, albumDao: AlbumDao, reviewDao: ReviewDao)extends AbstractController(cc) with I18nSupport {

  val reviewForm = Form(
    mapping(
      "albumId" -> longNumber,
      "userId" -> longNumber,
      "content" -> nonEmptyText)
    ((albumId, userId, content) => Review(None, albumId, userId, LocalDateTime.now(), content))
    ((r: Review) => Some(r.albumId, r.userId, r.content))
  )

  def albumHome(id: Long) = Action { implicit c =>
    val album: Album = albumDao.getAlbum(id)
    val reviews: List[Review] = reviewDao.getAllReviews(id)
    Ok(views.html.albumHome(reviews, album, reviewForm, ""))
  }

  def addReview(albumId: Long) = Action { implicit request =>
    val album: Album = albumDao.getAlbum(albumId)
    val reviews: List[Review] = reviewDao.getAllReviews(albumId)
    reviewForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.albumHome(reviews, album, reviewForm, ""))),
      form => {
        val newlyAddedId = reviewDao.saveReview(form)
        newlyAddedId match {
          case Some(x) => Redirect(routes.AlbumController.albumHome(form.albumId))
          case None => Ok(views.html.albumHome(reviews, album, reviewForm, ""))
        }
      }
    )
  }
}
