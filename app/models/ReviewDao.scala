package models

import javax.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._

class ReviewDao @Inject()(db: Database){

  def getAllReviews(albumId: Long) = {
    val parser = getReviewParser()

    val results = db.withConnection { implicit c =>
      SQL("select * from reviews o where o.album_id = {id}")
        .on("id" -> albumId)
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
        int("album_id") ~
        int("user_id") ~
        str("content")
    } map {
      case id ~ album ~ user ~ content => Review(Some(id), album, user, content)
    }
    parser
  }

  //boilerplate, but this isnt right
  def saveReview(review: Review): Option[Long] = {
    val result = db.withConnection{ implicit c =>
      SQL(s"insert into review (user_id, content) values ({album}, {user}, {content})")
          .on("album" -> review.albumId,
          "user" -> review.userId,
          "content" -> review.content)
        .executeInsert()
    }

    result
  }

}
