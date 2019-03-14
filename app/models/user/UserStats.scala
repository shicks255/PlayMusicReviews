package models.user

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class UserStats(reviews: Int, fiveStars: Int, averageRating: Double, averageReviewLength: Int, lastReview: LocalDateTime) {
  def lastReviewFormatted = {
    val dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm ")
    dtf.format(lastReview)
  }
}
