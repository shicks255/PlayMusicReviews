package controllers

import com.google.inject.{Inject, Singleton}
import models.{Artist, ArtistDao}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class ArtistController @Inject()(cc: ControllerComponents, artistDao: ArtistDao) extends AbstractController(cc) with I18nSupport{

  val artistSearchForm = Form(
    single("name" -> text)
  )

//  val artistSearchForm = Form(
//    mapping(
//      "name" -> nonEmptyText(1))
//    ((name) => Artist(None, name))
//    ((a: Artist) => Some(a.name))
//  )

  def artistSearchHome(artist: List[Artist]) = Action { implicit request =>
    Ok(views.html.artistSearch(artistSearchForm)(artist))
  }

  def searchForArtist() = Action {implicit request =>
    artistSearchForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.artistSearch(errors)(Nil))),
      form => {
        val artists: List[Artist] = artistDao.searchArtists(form);
        Redirect(routes.ArtistController.artistSearchHome(artists))
      }
    )
  }

}
