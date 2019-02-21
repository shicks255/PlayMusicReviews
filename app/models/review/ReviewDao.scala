package models.review

import java.time.ZoneId

import anorm.SqlParser._
import anorm._
import com.google.inject.Singleton
import javax.inject.Inject
import models.album.{Album, AlbumDao, AlbumFull}
import models.user.{User, UserDao}
import play.api.db.Database

@Singleton
class ReviewDao @Inject()(db: Database, albumDao: AlbumDao, userDao: UserDao){

  def getFullReview(review: Review): Option[ReviewFull] = {
    val user: User = userDao.getUserFromId(review.userId)
    val album: Option[Album] = albumDao.getAlbum(review.albumId)
    val fullAlbum = album match {
      case Some(x) => albumDao.getFullAlbum(x)
      case _ => None
    }

    fullAlbum match {
      case e: AlbumFull => Some(ReviewFull(review.id.get, e, user, review.addedOn, review.content))
      case _ => None
    }
  }

  def getUserReviews(userId: Long): List[Review] = {
    val parser = getReviewParser()

    val results = db.withConnection{ implicit c =>
      SQL("select * from reviews o where o.user_id = {id}")
        .on("id" -> userId)
        .as(parser.*)
    }

    results.sorted
  }

  def getAllReviews(albumId: Long): List[Review] = {
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
