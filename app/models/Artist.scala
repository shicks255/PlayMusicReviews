package models

case class Artist(id: Option[Long],
                  name: String,
                  mbid: String,
                  summary: String,
                  content: String)