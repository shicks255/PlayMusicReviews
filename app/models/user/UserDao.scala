package models.user

import anorm.SqlParser._
import anorm._
import com.google.inject.Inject
import play.api.db.Database
import play.api.mvc.{AnyContent, Request}

class UserDao @Inject()(db: Database){

  def registerUser(user: User): Option[Long] = {
    if (!userExists(user.username))
    {
      val result = db.withConnection { implicit c =>
        SQL(s"insert into users (username, password, is_admin) values({user}, {pass}, false)")
          .on("user" -> user.username, "pass" -> user.password)
          .executeInsert()
      }
      result
    }
    else
      None
  }

  def loginUser(user: User, request: Request[AnyContent]): Option[Long] = {
    val aUser: Option[User] = getUserFromDatabase(user.username)

    val dbUser = aUser match {
      case Some(x) if x.password == user.password => Some(x.id)
      case _ => None
    }
    //    dbUser match {
    //      case Some(x) => request.session.+(("userId", x.toString))
    //      case _ =>
    //    }

    dbUser
  }

  def getParser = {
    val parser = (
      str("username") ~
        str("password") ~
        int("id") ~
        get[Option[String]]("email_address") ~
        bool("email_list") ~
        bool("is_admin")) map {
      case username ~ password ~ id ~ emailAddress ~ emailList ~ isAdmin => User(username.trim, password.trim, id, emailAddress, emailList, isAdmin)
    }
    parser
  }

  def getUserFromDatabase(username: String): Option[User] = {
    val parser = getParser

    val result = db.withConnection { implicit c =>
      SQL("select * from users o where o.username = {uname}")
        .on("uname" -> username)
        .as(parser.*)
    }

    //not sure what to do to check if there are multiple users in this list
    result match {
      case h :: tail => Some(h)
      case _ => None
    }
  }

  def getUserFromId(id: Long): User = {
    val parser = getParser
    val result = db.withConnection{implicit c =>
      SQL("select * from users o where o.id = {id}")
        .on("id" -> id)
        .as(parser.*)
    }

    result.head
  }

  def getAllUsers() = {
    val parser = getParser
    val result = db.withConnection{implicit c =>
      SQL("select * from users o")
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

  def updateUser(form: EditUserForm) = {
    db.withConnection { implicit c =>
      SQL("update users set EMAIL_ADDRESS={email}, EMAIL_LIST={emailList} where ID={id}")
        .on("email" -> form.email,
          "emailList" -> form.emailList,
          "id" -> form.userId)
        .executeUpdate()
    }
  }

}