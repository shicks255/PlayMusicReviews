package controllers

import javax.inject._
import models.album.AlbumDao
import models.review.ReviewDao
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, reviewDao: ReviewDao, albumDao: AlbumDao) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(msg: Option[String]) = Action { implicit request =>
    val message: String = msg match {
      case Some(x) => x
      case None    => ""
    }

    val recentReviews = reviewDao.getAllReviews()
    val fullRecents = for {
      r <- recentReviews
      a <- albumDao.getAlbum(r.albumId)
      f <- albumDao.getFullAlbum(a)
      fr <- reviewDao.getFullReview(r, f)
    } yield fr

    Ok(views.html.index(message, fullRecents.reverse.take(4))(request.session))
  }
}
