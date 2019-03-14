package models.review

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class Review(id: Option[Long], albumId: Long, userId: Long, addedOn: LocalDateTime, content: String, rating: Float) extends Ordered[Review]{

  def compare(that: Review) = that.addedOn.compareTo(this.addedOn)

  def getFormattedDate(): String = {
    val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    try {
      dtf.format(addedOn)
    }
    catch {
      case e: Exception => ""
    }
  }

  def map[B](f: Review => B): B = {
    f(this)
  }

  def flatMap[B](f: Review => B): B = {
    f(this)
  }
}
