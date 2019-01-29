package models

case class Review(artist: String, album: String, user: String, content: String)

object Review {

  def saveReview(review: Review): String =
  {
    "Saving review for" + review.artist
  }

}