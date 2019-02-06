package models

import com.google.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._

class ArtistDao @Inject()(db: Database) {

  private def getArtistParser(): RowParser[Artist] = {
    val parser = (
      str("name") ~
      int("id")) map {
        case name ~ id => Artist(id, name)
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
      SQL("select * from artists o where o.name like %{name}%")
        .on("name" -> name)
        .as(parser.*)
    }

    results
  }

}
