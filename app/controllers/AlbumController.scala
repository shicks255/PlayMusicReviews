package controllers

import java.time.LocalDateTime

import com.google.inject.{Inject, Singleton}
import models.album.{Album, AlbumDao, AlbumFull}
import models.review.{Review, ReviewDao, ReviewFull}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, _}
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class AlbumController @Inject() (cc: ControllerComponents, albumDao: AlbumDao, reviewDao: ReviewDao) extends AbstractController(cc) with I18nSupport {

  val reviewForm = Form(
    mapping(
      "reviewId" -> optional(longNumber),
      "albumId" -> longNumber,
      "userId" -> longNumber,
      "content" -> nonEmptyText,
      "rating" -> default[BigDecimal](bigDecimal, 2.5))
    ((None, albumId, userId, content, rating) => Review(None, albumId, userId, LocalDateTime.now(), content, rating.toFloat))
    ((r: Review) => Some(r.id, r.albumId, r.userId, r.content, r.rating.toDouble))
  )

  def albumHome(id: Long) = Action { implicit request =>
    val album: Album = albumDao.getAlbum(id).get
    val fullAlbum: AlbumFull = albumDao.getFullAlbum(album)
    val reviews: List[Review] = reviewDao.getAllReviews(id)
    val fullReviews: List[Option[ReviewFull]] = reviews.map(reviewDao.getFullReview(_, fullAlbum))
    val rating = albumDao.getRating(fullAlbum)

    val userId = request.session.get("userId")
    if (userId.nonEmpty) {
      val myReview = reviews.filter(x => x.userId == userId.get.toLong)
      myReview match {
        case h :: _ => Ok(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm.fill(h), "", h.id))
        case _ => Ok(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm, "", None))
      }
    }
    else {
      Ok(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm, "", None))
    }
  }

  def addReview(albumId: Long, userReviewId: Option[Long]) = Action { implicit request =>
    val album: Album = albumDao.getAlbum(albumId).get
    val fullAlbum: AlbumFull = albumDao.getFullAlbum(album)
    val rating = albumDao.getRating(fullAlbum)
    val reviews: List[Review] = reviewDao.getAllReviews(albumId)
    val fullReviews: List[Option[ReviewFull]] = reviews.map(reviewDao.getFullReview(_, fullAlbum))
    reviewForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm, "", None))),
      form => {
        val newlyAddedId = userReviewId match {
          case None => reviewDao.saveReview(form)
          case Some(x) => reviewDao.updateReview(form)
        }
        Redirect(routes.AlbumController.albumHome(form.albumId))
      }
    )
  }

}
