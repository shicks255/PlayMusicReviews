package models.AlbumImage

import anorm.SqlParser._
import anorm._
import com.google.inject.{Inject, Singleton}
import models.ArtistImage.ArtistImage
import play.api.db.Database

@Singleton
class ArtistImageDao @Inject()(db: Database){

  def getArtistImageParser(): RowParser[ArtistImage] = {
    val parser = {
      int("artistId") ~
        str("text") ~
        str("url")} map {
      case id ~ text ~ url => ArtistImage(Some(id), text, url)
    }
    parser
  }

  def getArtistImagesArtist(artistId: Long): List[ArtistImage] = {
    val parser = getArtistImageParser()

    val results = db.withConnection{implicit c =>
      SQL("select * from artist_images o where o.artist_id = {id}")
        .on("id" -> artistId)
        .as(parser.*)
    }

    results
  }

  def saveArtistImage(image: ArtistImage) = {
    val result = db.withConnection{implicit c =>
      SQL("insert into artist_images (artist_id, text, url) values ({artistId}, {text}, {url}}))")
        .on("artistId" -> image.artistId,
          "text" -> image.text,
          "url" -> image.url)
        .executeInsert()
    }
  }

}
