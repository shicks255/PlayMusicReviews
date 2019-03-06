package controllers

import com.google.inject.Inject
import models.album.{Album, AlbumDao, AlbumFull}
import models.review.ReviewDao
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

class ReviewController @Inject()(cc: ControllerComponents, reviewDao: ReviewDao, albumDao: AlbumDao) extends AbstractController(cc) with I18nSupport{

  def topRated() = Action{implicit request =>
    val allAlbums: List[Album] = albumDao.getAllAlbums().filter(x => albumDao.getRating(x) > 0)
    val albumsAndRatings: List[(AlbumFull, Float)] = allAlbums
      .map(x => (albumDao.getFullAlbum(x), albumDao.getRating(x)))
      .sortWith((a,b) => a._2 > b._2)
      .take(10)

    Ok(views.html.topRated(albumsAndRatings))
  }

}
