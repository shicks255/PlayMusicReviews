package models.tasks.albumCheck

import java.time.LocalDate

import com.google.inject.Inject
import models.album.AlbumDao

class AlbumCheck @Inject()(albumDao: AlbumDao){

  def run = {
    println("running album check")
    val allAlbums = albumDao.getAllAlbums().map(x => albumDao.getFullAlbum(x))
    val badDates = allAlbums.filter(x => x.releaseDate == LocalDate.of(1900, 1, 1))

    val missingTracks = for {
      x <- allAlbums
      if (x.tracks.size <= 1)
    } yield x

    if (badDates.size > 0)
      {
        println("bad dates")
        badDates.foreach(x => println(s"${x.artist.name} - ${x.name}"));
      }
    if (missingTracks.size > 0)
      {
        println("missing tracks")
        missingTracks.foreach(x => println(s"${x.artist.name} - ${x.name}"));
      }
  }

}
