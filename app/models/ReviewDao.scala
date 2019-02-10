package models

import java.time.ZoneId

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

    results.sorted
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
        str("content") ~
        date("added_on")
    } map {
      case id ~ album ~ user ~ content ~ date => Review(Some(id), album, user, date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime, content)
    }
    parser
  }

  //boilerplate, but this isnt right
  def saveReview(review: Review): Option[Long] = {
    val result = db.withConnection{ implicit c =>
      SQL(s"insert into reviews (album_id, user_id, content, added_on) values ({album}, {user}, {content}, {addedOn})")
          .on("album" -> review.albumId,
          "user" -> review.userId,
          "content" -> review.content,
          "addedOn" -> review.addedOn)
        .executeInsert()
    }

    result
  }

}
