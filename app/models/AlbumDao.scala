package models

import anorm.RowParser
import com.google.inject.{Inject, Singleton}
import play.api.db.Database
import anorm._
import anorm.SqlParser._

@Singleton
class AlbumDao @Inject()(db: Database, artistDao: ArtistDao){

  def getFullAlbum(album: Album) = {
    val artist: Artist = artistDao.getArtist(album.artistId).get
    AlbumFull(album.id.get, album.name, album.year, artist, album.mbid, album.url, album.imageSmall, album.imageMed, album.imageLarge)
  }

  def getAlbumParser(): RowParser[Album] = {
    val parser = (
      str("name") ~
        int("id") ~
        int("artist_id") ~
        int("year") ~
        str("mbid") ~
        str("url") ~
        str("image_small") ~
        str("image_med") ~
        str("image_large")) map {
      case name ~ id ~ artistId ~ year ~ mbid ~ url ~ small ~ med ~ large =>
        Album(Some(id), name, year, artistId, mbid, url, small, med, large)
    }
    parser
  }

  def getAlbumsFromArtist(artistId: Long) = {
    val parser: RowParser[Album] = getAlbumParser()

    val result = db.withConnection{ implicit c =>
      SQL("select * from albums o where o.artist_id = {id}")
        .on("id" -> artistId)
        .as(parser.*)
    }
    result
  }

  def searchAlbumName(name: String): List[Album] = {
    val parser: RowParser[Album] = getAlbumParser()

    val result = db.withConnection{ implicit c =>
      SQL("select * from albums o where o.name like % {name} %")
        .on("name" -> name)
        .as(parser.*)
    }
    result
  }

  def getAlbum(id: Long): Album = {
    val parser: RowParser[Album] = getAlbumParser()

    val result = db.withConnection { implicit c =>
      SQL("select * from albums o where o.id = {id}")
        .on("id" -> id)
        .as(parser.*)
    }

    return result.head
  }

  def saveAlbum()

}
