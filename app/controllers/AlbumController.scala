package controllers

import com.google.inject.{Inject, Singleton}
import models.{Album, AlbumDao, Review, ReviewDao}
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class AlbumController @Inject() (cc: ControllerComponents, albumDao: AlbumDao, reviewDao: ReviewDao)extends AbstractController(cc) with I18nSupport{

  def albumHome(id: Long) = Action {implicit c =>
    val album: Album = albumDao.getAlbum(id)
    val reviews: List[Review] = reviewDao.getAllReviews(id)
    Ok(views.html.albumHome(reviews, album))
  }

}
