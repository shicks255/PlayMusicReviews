package models

import javax.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._

class ReviewDao @Inject()(db: Database){

  def getAllReviews(album: String) = {
    val parser = getReviewParser()

    val results = db.withConnection { implicit c =>
      SQL("select * from reviews o where o.album = {album}")
        .on("album" -> album)
        .as(parser.*)
    }

    results
  }

  def getReview(id: Long) = {
    val parser = getReviewParser()

    val result = db.withConnection { implicit c =>
      SQL("select * from reviews o where o.id = {id}")
        .on("id" -> id)
        .as(parser.*)
    }

    result match {
      case h :: tail => Some(h)
      case _ => None
    }
  }

  def getReviewParser(): RowParser[Review] = {
    val parser = {
      int("id") ~
        str("artist") ~
        str("album") ~
        str("user") ~
        str("content")
    } map {
      case id ~ artist ~ album ~ user ~ content => Review(Some(id), artist, album, user, content)
    }
    parser
  }

  //boilerplate, but this isnt right
  def saveReview(review: Review): Option[Long] = {
    val result = db.withConnection{ implicit c =>
      SQL(s"insert into review (artistid, albumid, userid, content) " +
        s"values ({artist}, {album}, {user}, {content})")
        .on("artist" -> review.artist,
          "album" -> review.album,
          "user" -> review.user,
          "content" -> review.content)
        .executeInsert()
    }

    result
  }

}
