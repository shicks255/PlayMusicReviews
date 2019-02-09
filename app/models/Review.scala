package models

import java.time.LocalDateTime

case class Review(id: Option[Long], albumId: Long, userId: Long, addedOne: LocalDateTime, content: String)
