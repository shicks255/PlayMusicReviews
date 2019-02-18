package models.Review

import java.time.LocalDateTime

import models.Album.AlbumFull
import models.User.User

case class ReviewFull(id: Long, album: AlbumFull, user: User, addedOn: LocalDateTime, content: String)
