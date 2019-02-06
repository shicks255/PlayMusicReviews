package models

import anorm.RowParser
import com.google.inject.{Inject, Singleton}
import play.api.db.Database
import anorm._
import anorm.SqlParser._

@Singleton
class AlbumDao @Inject()(db: Database){

  def getAlbumParser(): RowParser[Album] = {
    val parser = (
      str("name") ~
        int("id") ~
        int("artistId") ~
        int("year")) map {
      case name ~ id ~ artistId ~ year => Album(id, name, year, artistId)
    }
    parser
  }

  def getAlbumsFromArtist(artistId: Long) = {
    val parser: RowParser[Album] = getAlbumParser()

    val result = db.withConnection{ implicit c =>
      SQL("select * from albums o where o.artistId = {id}")
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

}
