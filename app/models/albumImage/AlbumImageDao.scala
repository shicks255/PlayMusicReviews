package models.albumImage

import anorm.SqlParser._
import anorm._
import com.google.inject.{Inject, Singleton}
import play.api.db.Database

@Singleton
class AlbumImageDao @Inject()(db: Database){

  def getAlbumImageParser(): RowParser[AlbumImage] = {
    val parser = {
      int("album_id") ~
        str("text") ~
        str("url")} map {
      case id ~ text ~ url => AlbumImage(Some(id), text, url)
    }
    parser
  }

  def getAlbumImagesFromAlbum(albumId: Long): List[AlbumImage] = {
    val parser = getAlbumImageParser()

    val results = db.withConnection{implicit c =>
      SQL("select * from album_images o where o.album_id = {id}")
        .on("id" -> albumId)
        .as(parser.*)
    }

    results
  }

  def saveAlbumImage(image: AlbumImage) = {
    val result = db.withConnection{implicit c =>
      SQL("insert into album_images (album_id, text, url) values ({albumId}, {text}, {url})")
        .on("albumId" -> image.albumId,
          "text" -> image.text,
          "url" -> image.url)
        .executeInsert()
    }
  }

}
