package models

case class AlbumFull(id: Long, name: String, year: Int, artist: Artist, mbid: String, url: String, imageSmall: String, imageMed: String, imageLarge: String)
