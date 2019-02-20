package models.artistImage

import models.Image

case class ArtistImage(artistId: Option[Long], size: String, url: String) extends Image {
  override def getUrl: String = url
  override def getSize: String = size
}
