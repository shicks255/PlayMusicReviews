package controllers

import com.google.inject.{Inject, Singleton}
import com.steven.hicks.beans.album.Album
import models.LastFMDao
import models.album.AlbumDao
import models.artist.{Artist, ArtistDao}
import models.review.ReviewDao
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ArtistController @Inject()(cc: ControllerComponents, artistDao: ArtistDao, albumDao: AlbumDao, lastFMDao: LastFMDao, reviewDao: ReviewDao) extends AbstractController(cc) with I18nSupport{

  val artistSearchForm = Form(
    single("name" -> text)
  )

  def artistSearchHome() = Action { implicit request =>
    Ok(views.html.artistSearch(artistSearchForm))
  }

  def searchForArtist() = Action  {implicit request =>
    artistSearchForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.artistSearch(errors))),
      form => {
        val artists: List[Artist] = artistDao.searchArtists(form);
        val filteredArtists = artists.filter(a => {
          a.id match {
            case Some(x) => true
            case _ => false
          }
        }).map(artistDao.getFullArtist(_))

        val names = filteredArtists.map(_.name)
        val nonDBArtists = lastFMDao.searchForLastFMArtists(form)
        val filteredNonDBArtists = nonDBArtists.filterNot(x => names.contains(x.getName) || x.getMbid.length == 0)
        Ok(views.html.artistSearchResults(filteredArtists, filteredNonDBArtists, form))
      }
    )
  }

  def artistHome(id: Long) = Action.async{implicit request =>
    val artist = artistDao.getArtist(id)
    val fullArtist = artistDao.getFullArtist(artist.get)
    val albums = albumDao.getAlbumsFromArtist(id).map(albumDao.getFullAlbum).sorted
    val albumNames = albums.map(_.name)
    val albumsWithRatings = albums.map(a => (a, albumDao.getRating(a)))
    implicit val g:ExecutionContext = global

    val maybeNonDBAlbums: Future[List[Album]] = lastFMDao.searchForLastFMAlbums(artist.get.mbid, artist.get.name)
    maybeNonDBAlbums map { album =>
      album match {
        case a: List[Album] => {
          val nonDBAlbums = a.filter(x => x != null && x.getName.length > 0)
          val filteredNonDBAlbums = nonDBAlbums.filterNot(x => albumNames.contains(x.getName))
          Ok(views.html.artistHome(albumsWithRatings, filteredNonDBAlbums, fullArtist))
        }
        case _ => Redirect(routes.ArtistController.artistSearchHome())
      }
    }
  }

  def addAlbumToDatabase(artistId: Long, mbid: String, albumTitle: String) = Action{ implicit request =>
    val artist = artistDao.getArtist(artistId)
    val newlyCreatedId: Option[Long] = albumDao.saveAlbum(artistId, mbid, albumTitle, artist.get.name)
    newlyCreatedId match {
      case Some(x) => Redirect(routes.AlbumController.albumHome(x))
      case _ => Redirect(routes.ArtistController.artistHome(artistId))
    }
  }

  def createArtist(mbid: String) = Action {implicit request =>
    val newArtistId: Option[Long] = lastFMDao.saveLastFMArtist(mbid)
    newArtistId match {
      case Some(x) => Redirect(routes.ArtistController.artistHome(newArtistId.get))
      case _ => Redirect(routes.ArtistController.artistSearchHome())
    }
  }

}
