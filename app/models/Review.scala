package models

case class Review(id: Option[Long], albumId: Long, userId: Long, content: String)
