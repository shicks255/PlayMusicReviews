package models

import com.google.inject.Inject
import com.steven.hicks.beans.ArtistAlbums
import com.steven.hicks.beans.album.Album
import com.steven.hicks.logic.{AlbumSearcher, ArtistQueryBuilder, ArtistSearcher}
import models.album.AlbumDao
import models.albumImage.{AlbumImageDao, ArtistImageDao, TrackDao}
import models.artist.{Artist, ArtistDao}
import models.artistImage.ArtistImage
import play.api.cache._

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class LastFMDao @Inject()(artistDao: ArtistDao, albumDao: AlbumDao, trackDao: TrackDao,
                          artistImageDao: ArtistImageDao, albumImageDao: AlbumImageDao,
                          cache: AsyncCacheApi){

  def searchForLastFMArtists(name: String): List[com.steven.hicks.beans.artist.Artist] = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().artistName(name).build()
    val searcher: ArtistSearcher = new ArtistSearcher
    val artists = searcher.searchForArtists(builder).asScala
    artists.toList
  }

  def searchForLastFMAlbums(mbid: String, name: String): Future[List[com.steven.hicks.beans.album.Album]] = {
    implicit val global: ExecutionContext = scala.concurrent.ExecutionContext.global
    val result: Future[List[Album]] = cache.getOrElseUpdate[List[Album]](name){
      println("putting " + name + " into cache")
      val query = new ArtistQueryBuilder.Builder().mbid(mbid).build()
      val searcher = new ArtistSearcher
      val albums: List[ArtistAlbums] = searcher.getAlbums(query).asScala.toList

      val albumSearcher = new AlbumSearcher
      val fullAlbums = albums.map(x => albumSearcher.getFullAlbum(x.getMbid, x.getName, name))
      Future.apply(fullAlbums)
    }
    result
  }

  def saveLastFMArtist(mbid: String) = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().mbid(mbid).setLimit(60).build()
    val searcher: ArtistSearcher = new ArtistSearcher

    val lastFMArtist: com.steven.hicks.beans.artist.Artist = searcher.getFullArtist(mbid)
    val artist: Artist = Artist(None, lastFMArtist.getName, mbid, lastFMArtist.getBio.getSummary, lastFMArtist.getBio.getContent)

    val id: Option[Long] = artistDao.saveArtist(artist)
    id match {
      case Some(x) => println("Artist created with ID " + x)
      case _ =>
    }

    val images: List[com.steven.hicks.beans.artist.Image] = lastFMArtist.getImage.toList
    for (x <- images)
      artistImageDao.saveArtistImage(ArtistImage(id, x.getSize, x.getText))

    id
  }

}
