package models

case class User(username: String, password: String, id: Integer = 0)

object User {

  //  implicit def toParameters: ToParameterList[User] =
  //    Macro.toParameters[User]

  def save(user: User):String = {

    ""
  }

  def update(user: User):String = {

    ""
  }

  def getUser(userName: String, password: String): User = {
    //do stuff to authenticate password
    User(userName, password)
  }

}