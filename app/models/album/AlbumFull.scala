package models.album

import models.artist.Artist

case class AlbumFull(id: Long, name: String, year: Int, artist: Artist, mbid: String, url: String)
