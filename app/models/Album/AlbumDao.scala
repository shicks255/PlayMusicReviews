package models.Album

import anorm.SqlParser._
import anorm.{RowParser, _}
import com.google.inject.{Inject, Singleton}
import com.steven.hicks.logic.AlbumSearcher
import models.Artist.{Artist, ArtistDao}
import models.{Artist, ArtistDao}
import play.api.db.Database

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

  def saveAlbum(artistId: Long, album: com.steven.hicks.beans.ArtistAlbums) = {

    val searcher = new AlbumSearcher
    val fullAlbum = searcher.getFullAlbum(album.getMbid)

    val result = db.withConnection{implicit c =>
      SQL("insert into albums (name,artist_id, mbid, url, image_small, image_med, image_large) values ({name},{artist_id},{mbid},{url},{small}, {med}, {large})")
          .on("name" -> fullAlbum.getName, "artist_id" -> artistId, "url" -> fullAlbum.getUrl, )
    }



  }

}
