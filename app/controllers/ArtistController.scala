package controllers

import com.google.inject.{Inject, Singleton}
import models.album.AlbumDao
import models.artist.{Artist, ArtistDao}
import models.LastFMDao
import models.review.ReviewDao
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class ArtistController @Inject()(cc: ControllerComponents, artistDao: ArtistDao, albumDao: AlbumDao, lastFMDao: LastFMDao, reviewDao: ReviewDao) extends AbstractController(cc) with I18nSupport{

  val artistSearchForm = Form(
    single("name" -> text)
  )

  val addArtistForm = Form{
    single("albumName" -> text)
  }

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

  def artistHome(id: Long) = Action {implicit request =>
    val artist = artistDao.getArtist(id)
    val fullArtist = artistDao.getFullArtist(artist.get)
    val albums = albumDao.getAlbumsFromArtist(id).map(albumDao.getFullAlbum).sorted
    val albumMBIDs = albums.map(_.mbid)
    val albumsWithRatings = albums.map(a => (a, albumDao.getRating(a)))

    val nonDBAlbums = lastFMDao.searchForLastFMAlbums(artist.get.mbid)
    val filteredNonDBAlbums = nonDBAlbums.filterNot(x => albumMBIDs.contains(x.getMbid))
    Ok(views.html.artistHome(albumsWithRatings, filteredNonDBAlbums, fullArtist, addArtistForm))
  }

  def addAlbumToDatabase(artistId: Long, mbid: String) = Action{ implicit request =>
    val newlyCreatedId: Option[Long] = albumDao.saveAlbum(artistId, mbid)
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

  def submitAlbum(id: Long) = Action {implicit request =>
    addArtistForm.bindFromRequest().fold(
      errors => Redirect(routes.ArtistController.artistHome(id)),
      form => {
        //:todo logic to suggest an album
      }
    )
    Redirect(routes.ArtistController.artistHome(id))
  }

}
