package models.user

case class User(username: String, password: String, id: Long = 0, emailAddress: Option[String], emailList: Boolean)
