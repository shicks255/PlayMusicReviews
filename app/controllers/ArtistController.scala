package controllers

import com.google.inject.{Inject, Singleton}
import models.album.AlbumDao
import models.artist.{Artist, ArtistDao}
import models.LastFMDao
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class ArtistController @Inject()(cc: ControllerComponents, artistDao: ArtistDao, albumDao: AlbumDao, lastFMDao: LastFMDao) extends AbstractController(cc) with I18nSupport{

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
        val filteredArtists = artists.filter(x => {
          x.id match {
            case Some(x) => true
            case _ => false
          }
        })

        val names = filteredArtists.map(_.name)
        val nonDBArtists = lastFMDao.searchForLastFMArtists(form)
        val filteredNonDBArtists = nonDBArtists.filterNot(x => names.contains(x.getName))
        Ok(views.html.artistSearchResults(filteredArtists, filteredNonDBArtists, form))
      }
    )
  }

  def artistHome(id: Long) = Action {implicit request =>
    val artist = artistDao.getArtist(id)
    val albums = albumDao.getAlbumsFromArtist(id)
    val fullAlbums = albums.map(albumDao.getFullAlbum(_))
    Ok(views.html.artistHome(fullAlbums, artist.get))
  }

  def createArtist(mbid: String) = Action {implicit request =>

    val newArtistId: Option[Long] = lastFMDao.saveLastFMArtist(mbid)
    newArtistId match {
      case Some(x) => Redirect(routes.ArtistController.artistHome(newArtistId.get))
      case _ => Redirect(routes.ArtistController.artistSearchHome())
    }
  }

}
