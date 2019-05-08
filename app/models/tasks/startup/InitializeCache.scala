package models.tasks.startup

import com.google.inject.Inject
import com.steven.hicks.beans.album.Album
import models.LastFMDao
import models.artist.{Artist, ArtistDao}
import play.api.Configuration
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

//if the cache gets too big, just pull in the artists/albums that have reviews first, and then the next x up to 50 or something
class InitializeCache @Inject()(db: Database, config: Configuration, artistDao: ArtistDao, lastFMDao: LastFMDao) {

  def run() = {
    println("initializing cache....")
    val artists: List[Artist] = artistDao.getAllArtists()

    implicit val global: ExecutionContext = scala.concurrent.ExecutionContext.global

    val result: List[Future[List[Album]]] = artists.map(x => lastFMDao.searchForLastFMAlbums(x.mbid, x.name))
    val cacheResult: Future[List[List[Album]]] = Future.sequence(result)
   cacheResult
  }
}
