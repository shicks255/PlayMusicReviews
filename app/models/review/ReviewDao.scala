package models.review

import java.time.ZoneId

import anorm.SqlParser._
import anorm._
import com.google.inject.Singleton
import javax.inject.Inject
import models.album.AlbumFull
import models.user.{User, UserDao, UserStats}
import play.api.db.Database

@Singleton
class ReviewDao @Inject()(db: Database, userDao: UserDao){

  def getFullReview(review: Review, a: AlbumFull): Option[ReviewFull] = {
    val user: User = userDao.getUserFromId(review.userId)
    Some(ReviewFull(review.id.get, a, user, review.addedOn, review.content, review.rating))
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

  def getAllReviews() = {
    val parser = getReviewParser()

    val results = db.withConnection{ implicit c =>
      SQL("select * from reviews order by added_on")
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
        str("content") ~
        date("added_on") ~
        float("rating")
    } map {
      case id ~ album ~ user ~ content ~ date ~ rating => Review(Some(id), album, user, date.toInstant.atZone(ZoneId.systemDefault()).toLocalDateTime, content, rating)
    }
    parser
  }

  def saveReview(review: Review): Option[Long] = {
    val result = db.withConnection{ implicit c =>
      SQL(s"insert into reviews (album_id, user_id, content, added_on, rating) values ({album}, {user}, {content}, {addedOn}, {rating})")
          .on("album" -> review.albumId,
          "user" -> review.userId,
          "content" -> review.content,
          "addedOn" -> review.addedOn,
          "rating" -> review.rating)
        .executeInsert()
    }

    result
  }

  def updateReview(review: Review) = {
    val result = db.withConnection{implicit c =>
      SQL("update reviews set content={content}, rating={rating} where id={id}")
        .on("content" -> review.content,
            "rating" -> review.rating,
            "id" -> review.id)
        .executeUpdate()
    }
  }

  def getRating(id: Long) = {
    val reviews = getAllReviews(id)
    val total = reviews.map(_.rating).foldLeft(0.0)(_+_)
    total/reviews.size
  }

  def getUserStats(id: Long): UserStats = {
    val reviews = getUserReviews(id)
    val fiveStars = reviews.filter(x => x.rating >= 5.0)
    val avgRating = reviews.map(x => x.rating).foldLeft(0.0)(_+_)/reviews.size
    val formatter = java.text.NumberFormat.getInstance()
    val avgLength = reviews.map(r => r.content).map(x => x.split("\\s+").size).foldLeft(0)(_+_)/reviews.size
    val latest = reviews.sortWith((x,y) => x.addedOn.isAfter(y.addedOn))

    UserStats(reviews.size, fiveStars.size, formatter.format(avgRating).toDouble, avgLength, latest.head.addedOn)
  }

}
