package models.review

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import models.album.AlbumFull
import models.user.User

case class ReviewFull(id: Long, album: AlbumFull, user: User, addedOn: LocalDateTime, content: String, rating: Float) extends Ordered[ReviewFull]{
  def formatDate(): String = {
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    dtf.format(addedOn)
  }

  override def compare(that: ReviewFull): Int = this.addedOn.compareTo(that.addedOn)
}
