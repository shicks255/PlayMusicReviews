package models.tasks.startup

import com.google.inject.Inject
import com.steven.hicks.beans.album.Album
import models.LastFMDao
import models.album.AlbumDao
import models.artist.ArtistDao
import models.review.ReviewDao
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

//if the cache gets too big, just pull in the artists/albums that have reviews first, and then the next x up to 50 or something
class InitializeCache @Inject()(db: Database, artistDao: ArtistDao, albumDao: AlbumDao, lastFMDao: LastFMDao, reviewDao: ReviewDao) {

  def run() = {
    println("initializing cache....")
    implicit val global: ExecutionContext = scala.concurrent.ExecutionContext.global
    val reviews = reviewDao.getAllReviews()
    val albums = reviews.map(x => albumDao.getAlbum(x.albumId))
    val artists = albums.map(x => artistDao.getArtist(x.get.artistId).get).distinct.slice(0, 50)

    val result: List[Future[List[Album]]] = artists.map(x => lastFMDao.searchForLastFMAlbums(x.mbid, x.name))
    val cacheResult: Future[List[List[Album]]] = Future.sequence(result)
   cacheResult
  }
}
