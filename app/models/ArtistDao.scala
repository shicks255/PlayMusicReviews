package models

import com.google.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._
import com.steven.hicks.logic.{ArtistQueryBuilder, ArtistSearcher}
import scala.collection.JavaConverters._

class ArtistDao @Inject()(db: Database, albumDao: AlbumDao) {

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
      SQL("select * from artists o where o.name = {name}")
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

  def searchForLastFMArtists(name: String): List[com.steven.hicks.beans.Artist] = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().artistName(name).build()
    val searcher: ArtistSearcher = new ArtistSearcher
    val artists = searcher.search(builder).asScala
    artists.toList
  }

  def createAlbumsForNewArtist(mbid: String, id: Long) = {

    val query: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().mbid(mbid).build()
    val searcher: ArtistSearcher = new ArtistSearcher

    val albums = searcher.getAlbums(query)

  }

  def createArtist(mbid: String) = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().mbid(mbid).build()
    val searcher: ArtistSearcher = new ArtistSearcher

    val lastFMArtist: com.steven.hicks.beans.Artist = searcher.getFullArtist(mbid)
    val artist: Artist = Artist(None, lastFMArtist.getName, mbid, lastFMArtist.getBio.getSummary, lastFMArtist.getBio.getContent)

    val id: Option[Long] = saveArtist(artist)
    id match {
      case Some(x) => println("Artist created with ID " + x)
      case _ =>
    }

    if (id.nonEmpty)
      createAlbumsForNewArtist(mbid, id.get)

    id
  }

}
