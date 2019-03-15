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

    println("bad dates")
    badDates.foreach(x => println(x.name))
    println("missing tracks")
    missingTracks.foreach(x => println(x.name))
  }

}
