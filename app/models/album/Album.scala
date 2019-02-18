package models.album

case class Album(id: Option[Long],
                 name: String,
                 year: Int,
                 artistId: Long,
                 mbid: String,
                 url: String)
