package models

case class Review(id: Option[Long], artist: String, album: String, user: String, content: String)
