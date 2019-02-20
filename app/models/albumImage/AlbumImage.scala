package models.albumImage

import models.Image

case class AlbumImage(albumId: Option[Long], size: String, url: String) extends Image {
  override def getSize: String = size
  override def getUrl: String = url
}
