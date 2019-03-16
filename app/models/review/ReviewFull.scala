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

  def getColorClass = {
    rating match {
      case rating if rating >= 3.5 => "is-success"
      case rating if rating >= 2.0  && rating < 3.5 => "is-warning"
      case rating if rating < 2.0 => "is-danger"
      case _ => "is-light"
    }
  }
}
