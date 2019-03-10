package models.album

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import models.ImageGetter
import models.albumImage.AlbumImage
import models.artist.Artist
import models.track.Track

case class AlbumFull(id: Long,
                     name: String,
                     releaseDate: LocalDate,
                     artist: Artist,
                     mbid: String,
                     url: String,
                     images: List[AlbumImage],
                     tracks: List[Track]) extends ImageGetter with Ordered[AlbumFull] {

  override def compare(that: AlbumFull): Int = this.releaseDate.compareTo(that.releaseDate)
  def getFormattedReleaseDate = {
    val dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    dtf.format(releaseDate)
  }

  def getFormattedReleaseDate2 = {
    val dtf = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    dtf.format(releaseDate)
  }

  def flatMap[B](f: AlbumFull => B): B = {
    f(this)
  }

}