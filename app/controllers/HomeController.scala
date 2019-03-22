package controllers

import javax.inject._
import models.SystemInfo
import models.album.AlbumDao
import models.review.ReviewDao
import models.user.UserDao
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, reviewDao: ReviewDao, albumDao: AlbumDao, userDao: UserDao) extends AbstractController(cc) {

  def index() = Action { implicit request =>
    val recentReviews = reviewDao.getAllReviews()
    val fullRecents = for {
      r <- recentReviews
      a <- albumDao.getAlbum(r.albumId)
      f <- albumDao.getFullAlbum(a)
      fr <- reviewDao.getFullReview(r, f)
    } yield fr

    val users = userDao.getAllUsers()
    val albums = albumDao.getAllAlbums()
    val reviews = reviewDao.getAllReviews()
    val ratings = reviews.filter(x => x.rating == x.rating)
    val fiveStars = reviews.filter(x => x.rating >= 5.0)
    val systemInfo = SystemInfo(users.size, albums.size, reviews.size, ratings.size, fiveStars.size)

    Ok(views.html.index(fullRecents.reverse.take(4), systemInfo))
  }

  def about() = Action { implicit request =>
    Ok(views.html.about()(request.session, request.flash))
  }
}
