package models.user

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

case class UserStats(reviews: Int, fiveStars: Int, averageRating: Double, averageReviewLength: Int, lastReview: Option[LocalDateTime]) {

  def lastReviewFormatted = {
    val dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mma ")
    lastReview match {
      case Some(x) => dtf.format(x)
      case _ => "no reviews"
    }
  }
}
