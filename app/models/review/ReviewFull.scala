package models.review

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import models.album.AlbumFull
import models.user.User

case class ReviewFull(id: Long, album: AlbumFull, user: User, addedOn: LocalDateTime, content: String) {
  def formatDate(): String = {
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    dtf.format(addedOn)
  }
}
