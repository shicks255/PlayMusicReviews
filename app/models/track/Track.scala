package models.track

case class Track(albumId: Option[Long], name: String, rank: Int, duration: Int)
