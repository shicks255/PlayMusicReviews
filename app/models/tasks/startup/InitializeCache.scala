package models.tasks.startup

import com.google.inject.Inject
import models.LastFMDao
import models.artist.{Artist, ArtistDao}
import play.api.Configuration
import play.api.db.Database

import scala.concurrent.ExecutionContext

class InitializeCache @Inject()(db: Database, config: Configuration, artistDao: ArtistDao, lastFMDao: LastFMDao) {

  def run() = {
    println("initializing cache....")
    val artists: List[Artist] = artistDao.getAllArtists()

    implicit val global: ExecutionContext = scala.concurrent.ExecutionContext.global

    artists.foreach(x => lastFMDao.searchForLastFMAlbums(x.mbid, x.name))

    println("cache initialized....")
  }
}
