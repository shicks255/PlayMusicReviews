package models

import java.time.LocalDateTime

case class ReviewFull(id: Long, album: AlbumFull, user: User, addedOn: LocalDateTime, content: String)

