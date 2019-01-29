package models

import com.google.inject.Inject
import play.api.db.Database
import anorm._
import anorm.SqlParser._

class UserDao @Inject()(db: Database){

  def registerUser(user: User): Option[Long] = {
    if (!userExists(user.username))
    {
      val result = db.withConnection { implicit c =>
        SQL(s"insert into users (username, password) values({user}, {pass})")
          .on("user" -> user.username, "pass" -> user.password)
          .executeInsert()
      }
      result
    }
    else
      None
  }

  def loginUser(user: User): Boolean = {
    val result = db.withConnection { implicit c =>
      SQL("select * from users where")
    }
    true
  }

  def getUser(username: String): List[User] = {
    val parser = (
      str("username") ~
        str("password") ~
        int("id")) map {
      case username ~ password ~ id => User(username, password, id)
    }

    val result = db.withConnection { implicit c =>
      SQL("select * from users o where o.username = {uname}")
        .on("uname" -> username)
        .as(parser.*)
    }
    result
  }

  def userExists(username: String): Boolean = {
    val result = db.withConnection{ implicit c =>
      SQL(s"select count(*) from users o where o.username = {uname}")
        .on("uname" -> username)
        .as(scalar[Long].single)
    }
    if (result > 0)
      true
    else false
  }
}