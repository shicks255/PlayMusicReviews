package models.album

import java.time.LocalDate

import anorm.SqlParser._
import anorm.{RowParser, _}
import com.google.inject.{Inject, Singleton}
import com.steven.hicks.logic.AlbumSearcher
import models.albumImage.{AlbumImage, AlbumImageDao, ArtistImageDao, TrackDao}
import models.artist.{Artist, ArtistDao}
import models.review.ReviewDao
import models.track.Track
import play.api.db.Database

@Singleton
class AlbumDao @Inject()(db: Database, artistDao: ArtistDao,trackDao: TrackDao, albumImageDao: AlbumImageDao, artistImageDao: ArtistImageDao){

  def getFullAlbum(album: Album): AlbumFull = {
    val artist: Artist = artistDao.getArtist(album.artistId).get
    val images: List[AlbumImage] = albumImageDao.getAlbumImagesFromAlbum(album.id.get)
    val tracks: List[Track] = trackDao.getTracksFromAlbum(album.id.get)
    val releasedate = if (album.releaseDate.nonEmpty) album.releaseDate.get else LocalDate.of(1900, 1, 1)
    AlbumFull(album.id.get, album.name, releasedate, artist, album.mbid, album.url, images, tracks)
  }

  def getAlbumParser(): RowParser[Album] = {
    val parser = (
      str("name") ~
        int("id") ~
        int("artist_id") ~
        get[LocalDate]("release_date") ~
        str("mbid") ~
        str("url")) map {
      case name ~ id ~ artistId ~ releaseDate ~ mbid ~ url => {
        Album(Some(id), name, Some(releaseDate), artistId, mbid, url)
      }
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

  def getAlbum(id: Long): Option[Album] = {
    val parser: RowParser[Album] = getAlbumParser()

    val result = db.withConnection { implicit c =>
      SQL("select * from albums o where o.id = {id}")
        .on("id" -> id)
        .as(parser.*)
    }

    result match {
      case h :: t => Some(h)
      case _ => None
    }
  }

  def saveAlbum(artistId: Long, mbid: String): Option[Long] = {
    val searcher = new AlbumSearcher
    val fullAlbum = searcher.getFullAlbum(mbid)
    val year: LocalDate = searcher.getAlbumDate(fullAlbum.getMbid)

    val result: Option[Long] = db.withConnection{implicit c =>
      SQL("insert into albums (name,artist_id, mbid, release_date, url) values ({name},{artist_id},{mbid},{releaseDate},{url})")
        .on("name" -> fullAlbum.getName,
          "artist_id" -> artistId,
          "mbid" -> fullAlbum.getMbid,
          "releaseDate" -> year.atStartOfDay(),
          "url" -> fullAlbum.getUrl)
        .executeInsert()
    }

    if (result.nonEmpty)
    {
      val images = fullAlbum.getImage
      images.map(x => AlbumImage(result, x.getSize, x.getText))
        .foreach(albumImageDao.saveAlbumImage(_))
      val tracks = fullAlbum.getTracks.getTrack
      tracks.map(x => Track(result, x.getName, x.getAttr.getRank.toInt, x.getDuration))
        .foreach(trackDao.saveTrack(_))
    }

    result
  }

}
