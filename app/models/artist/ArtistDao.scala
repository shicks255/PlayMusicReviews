package models.artist

import anorm.SqlParser._
import anorm._
import com.google.inject.Inject
import models.albumImage.ArtistImageDao
import models.artistImage.ArtistImage
import play.api.db.Database

class ArtistDao @Inject()(db: Database, artistImageDao: ArtistImageDao) {

  def getFullArtist(a: Artist): ArtistFull = {
    val images: List[ArtistImage] = artistImageDao.getArtistImagesArtist(a.id.get)
    ArtistFull(a.id.get, a.name, a.mbid, a.summary, a.content, images)
  }

  private def getArtistParser(): RowParser[Artist] = {
    val parser = (
      str("name") ~
      int("id") ~
      str("mbid") ~
      str("summary") ~
      str("content")) map {
        case name ~ id ~ mbid ~ summary ~ content => Artist(Some(id), name, mbid, summary, content)
      }
    parser
  }

  def getArtist(id: Long): Option[Artist] = {
    val parser: RowParser[Artist] = getArtistParser()

    val result = db.withConnection{ implicit c =>
      SQL("select * from artists o where o.id = {id}")
        .on("id" -> id)
        .as(parser.*)
    }

    result match {
      case h :: tail => Some(h)
      case _ => None
    }
  }

  def searchArtists(name: String): List[Artist] = {
    val parser: RowParser[Artist] = getArtistParser()

    val results = db.withConnection{ implicit c =>
      SQL("select * from artists o where lower(o.name) = lower({name})")
        .on("name" -> name)
        .as(parser.*)
    }

    results
  }

  def saveArtist(artist: Artist): Option[Long] = {

    //todo:make sure mbid doesnt exist
    val result = db.withConnection{ implicit c =>
      SQL("insert into artists (name, mbid, summary, content) values({name}, {mbid}, {summary}, {content})")
        .on("name" -> artist.name, "mbid" -> artist.mbid, "summary" -> artist.summary, "content" -> artist.content)
        .executeInsert()
    }
    result
  }

}
