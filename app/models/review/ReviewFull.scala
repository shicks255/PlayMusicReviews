package models.review

import java.time.LocalDateTime

import models.album.AlbumFull
import models.user.User

case class ReviewFull(id: Long, album: AlbumFull, user: User, addedOn: LocalDateTime, content: String)
