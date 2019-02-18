package models.album

import models.albumImage.AlbumImage
import models.artist.Artist
import models.track.Track

case class AlbumFull(id: Long,
                     name: String,
                     year: Int,
                     artist: Artist,
                     mbid: String,
                     url: String,
                     images: List[AlbumImage],
                     tracks: List[Track]) {
  def getMediumImage() = {

    val mediumImage = images.find(x => x.text == "medium")

    mediumImage match {
      case Some(x) => x
      case _ => images(0)
    }
  }
  def getLargeImage() = {
    val largeImage = images.find(x => x.text == "large")

    largeImage match {
      case Some(x) => x
      case _ => images(0)
    }
  }
}
