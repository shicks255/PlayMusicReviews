package models

import javax.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._

class ReviewDao @Inject()(db: Database){

  def getReview(id: Long) = {
    val parser = {
      int("id") ~
        str("artist") ~
        str("album") ~
        str("user") ~
        str("content")
    } map {
      case id ~ artist ~ album ~ user ~ content => Review(Some(id), artist, album, user, content)
    }

    val result = db.withConnection { implicit c =>
      SQL("select * from reviews o where o.id == {id}")
        .on("id" -> id)
        .as(parser.*)
    }

    result match {
      case h :: tail => Some(h)
      case _ => None
    }
  }

  def saveReview(review: Review): Option[Long] = {
    val result = db.withConnection{ implicit c =>
      SQL(s"insert into review (artist, album, user, content values ({artist}, {album}, {user}, {values})")
        .on("artist" -> review.artist, "album" -> review.user, "user" -> review.user, "content" -> review.content)
        .executeInsert()
    }

    result
  }

}
