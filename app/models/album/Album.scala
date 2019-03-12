package models.album

import java.time.LocalDate

case class Album(id: Option[Long],
                 name: String,
                 releaseDate: Option[LocalDate],
                 artistId: Long,
                 mbid: String,
                 url: String,
                 summary: String,
                 content: String)
