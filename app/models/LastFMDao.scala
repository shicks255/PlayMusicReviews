package models

import com.google.inject.Inject
import com.steven.hicks.beans.ArtistAlbums
import com.steven.hicks.logic.{AlbumSearcher, ArtistQueryBuilder, ArtistSearcher}
import models.album.AlbumDao
import models.albumImage.{AlbumImageDao, ArtistImageDao, TrackDao}
import models.artist.{Artist, ArtistDao}
import models.artistImage.ArtistImage

import scala.collection.JavaConverters._

class LastFMDao @Inject()(artistDao: ArtistDao, albumDao: AlbumDao, trackDao: TrackDao, artistImageDao: ArtistImageDao, albumImageDao: AlbumImageDao){

  def searchForLastFMArtists(name: String): List[com.steven.hicks.beans.Artist] = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().artistName(name).build()
    val searcher: ArtistSearcher = new ArtistSearcher
    val artists = searcher.search(builder).asScala
    artists.toList
  }

  def searchForLastFMAlbums(mbid: String): List[com.steven.hicks.beans.Album] = {
    val query = new ArtistQueryBuilder.Builder().mbid(mbid).build()
    val searcher = new ArtistSearcher
    val albums: List[ArtistAlbums] = searcher.getAlbums(query).asScala.toList

    val albumSearcher = new AlbumSearcher
    val fullAlbums = albums.map(x => albumSearcher.getFullAlbum(x.getMbid))
    fullAlbums
  }

  def saveLastFMArtist(mbid: String) = {
    val builder: ArtistQueryBuilder = new ArtistQueryBuilder.Builder().mbid(mbid).setLimit(60).build()
    val searcher: ArtistSearcher = new ArtistSearcher

    val lastFMArtist: com.steven.hicks.beans.Artist = searcher.getFullArtist(mbid)
    val artist: Artist = Artist(None, lastFMArtist.getName, mbid, lastFMArtist.getBio.getSummary, lastFMArtist.getBio.getContent)

    val id: Option[Long] = artistDao.saveArtist(artist)
    id match {
      case Some(x) => println("Artist created with ID " + x)
      case _ =>
    }

    val images: List[com.steven.hicks.beans.Artist.Image] = lastFMArtist.getImage.toList
    for (x <- images)
      artistImageDao.saveArtistImage(ArtistImage(id, x.getSize, x.getText))

    id
  }

}
