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

  def artistSearchHome() = Action { implicit request =>
    Ok(views.html.artistSearch(artistSearchForm))
  }

  def searchForArtist() = Action  {implicit request =>
    artistSearchForm.bindFromRequest().fold(
      errors => (BadRequest(views.html.artistSearch(errors))),
      form => {
        val artists: List[Artist] = artistDao.searchArtists(form);
        val filteredList = artists.filter(x => {
          x.id match {
            case Some(x) => true
            case _ => false
          }
        })
        Ok(views.html.artistSearchResults(filteredList))
      }
    )
  }

  def artistHome(id: Long) = Action {implicit request =>
    Ok(views.html.artistHome())
  }

}
