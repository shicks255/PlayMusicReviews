package controllers

import java.time.LocalDateTime
import java.util.Date

import com.google.inject.{Inject, Singleton}
import models.album.{Album, AlbumDao, AlbumFull}
import models.albumImage.TrackDao
import models.review.{Review, ReviewDao, ReviewFull}
import models.track.Track
import models.user.UserDao
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, _}
import play.api.i18n.I18nSupport
import play.api.mvc._

@Singleton
class AlbumController @Inject() (cc: ControllerComponents, albumDao: AlbumDao, reviewDao: ReviewDao, userDao: UserDao, trackDao: TrackDao) extends AbstractController(cc) with I18nSupport {
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

  val editDateForm = Form(
    single[Date]("releaseDate" -> date)
  )

  val addTrackForm = Form(
    mapping(
      "albumId" -> longNumber,
      "name" -> text,
      "rank" -> number,
      "duration" -> number)
    ((id, name, rank, duration) => Track(Some(id), name, rank, duration))
    ((t: Track) => Some(t.albumId.getOrElse(0), t.name, t.rank, t.duration))
  )

  def albumHome(id: Long) = Action { implicit request =>
    val album: Album = albumDao.getAlbum(id).get
    val fullAlbum: AlbumFull = albumDao.getFullAlbum(album)
    val reviews: List[Review] = reviewDao.getAllReviews(id)
    val fullReviews: List[Option[ReviewFull]] = reviews.map(reviewDao.getFullReview(_, fullAlbum))
    val rating = albumDao.getRating(fullAlbum)

    val userId = request.session.get("userId")
    if (userId.nonEmpty) {
      val user = userDao.getUserFromId(userId.get.toLong)
      val myReview = reviews.filter(x => x.userId == userId.get.toLong)
      myReview match {
        case h :: _ => Ok(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm.fill(h), "", h.id, editDateForm, addTrackForm, user.isAdmin))
        case _ => Ok(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm, "", None, editDateForm, addTrackForm, user.isAdmin))
      }
    }
    else {
      Ok(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm, "", None, editDateForm, addTrackForm, false))
    }
  }

  def addReview(albumId: Long, userReviewId: Option[Long]) = Action { implicit request =>
    val album: Album = albumDao.getAlbum(albumId).get
    val fullAlbum: AlbumFull = albumDao.getFullAlbum(album)
    val rating = albumDao.getRating(fullAlbum)
    val reviews: List[Review] = reviewDao.getAllReviews(albumId)
    val fullReviews: List[Option[ReviewFull]] = reviews.map(reviewDao.getFullReview(_, fullAlbum))

    val isAdmin = userReviewId match {
      case Some(x) => userDao.getUserFromId(x).isAdmin
      case _ => false
    }

    reviewForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.albumHome(fullReviews, fullAlbum, rating, reviewForm, "", None, editDateForm, addTrackForm, isAdmin))),
      form => {
        val newlyAddedId = userReviewId match {
          case None => reviewDao.saveReview(form)
          case Some(x) => reviewDao.updateReview(form)
        }
        Redirect(routes.AlbumController.albumHome(form.albumId))
      }
    )
  }

  def updateReleaseDate(albumId: Long) = Action{implicit request =>
    editDateForm.bindFromRequest().fold(
      errors => Redirect(routes.AlbumController.albumHome(albumId)),
      form => {
        albumDao.updateAlbumReleaseDate(albumId, form)
      }
    )
    Redirect(routes.AlbumController.albumHome(albumId))
  }

  def addAlbumTrack(albumId: Long) = Action{ implicit request =>
    addTrackForm.bindFromRequest().fold(
      errors => Redirect(routes.AlbumController.albumHome(albumId)),
      form => {
        trackDao.saveTrack(form)
      }
    )
    Redirect(routes.AlbumController.albumHome(albumId))
  }

}
