package models.Album

case class Album(id: Option[Long],
                 name: String,
                 year: Int,
                 artistId: Long,
                 mbid: String,
                 url: String,
                 imageSmall: String,
                 imageMed: String,
                 imageLarge: String)
