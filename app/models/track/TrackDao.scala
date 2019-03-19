package models.albumImage

import anorm.SqlParser._
import anorm._
import com.google.inject.{Inject, Singleton}
import models.track.Track
import play.api.db.Database

@Singleton
class TrackDao @Inject()(db: Database){

  def getTrackParser(): RowParser[Track] = {
    val parser = {
      int("album_id") ~
        str("name") ~
        int("rank") ~
        int("duration")} map {
      case id ~ name ~ rank ~ duration => Track(Some(id), name, rank, duration)
    }
    parser
  }

  def getTracksFromAlbum(albumId: Long): List[Track] = {
    val parser = getTrackParser()

    val results = db.withConnection{implicit c =>
      SQL("select * from tracks o where o.album_id = {id}")
        .on("id" -> albumId)
        .as(parser.*)
    }
    results.sortBy(x => x.rank)
  }

  def saveTrack(track: Track) = {
    val result = db.withConnection{implicit c =>
      SQL("insert into tracks (album_id, name, rank, duration) values ({albumId}, {name}, {rank}, {duration})")
        .on("albumId" -> track.albumId,
          "name" -> track.name,
          "rank" -> track.rank,
          "duration" -> track.duration)
        .executeInsert()
    }
  }

}
