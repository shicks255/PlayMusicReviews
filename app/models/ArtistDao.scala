package models

import com.google.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._
import com.steven.hicks.logic.{ArtistQueryBuilder, ArtistSearcher}
import scala.collection.JavaConverters._

class ArtistDao @Inject()(db: Database) {

  private def getArtistParser(): RowParser[Artist] = {
    val parser = (
      str("name") ~
      int("id")) map {
        case name ~ id => Artist(Some(id), name)
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
      SQL("select * from artists o where o.name = {name}")
        .on("name" -> name)
        .as(parser.*)
    }

    results
  }

  def searchForLastFMArtists(name: String): List[com.steven.hicks.beans.Artist] = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().artistName(name).build()
    val searcher: ArtistSearcher = new ArtistSearcher
    val artists = searcher.search(builder).asScala
    artists.toList
  }

}
