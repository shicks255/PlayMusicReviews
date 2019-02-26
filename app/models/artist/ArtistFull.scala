package models.artist

import models.ImageGetter
import models.artistImage.ArtistImage

case class ArtistFull(id: Long, name: String, mbid: String, summary: String, content: String, images: List[ArtistImage]) extends ImageGetter {

  def getSummary = {
    val start = summary.indexOf("<a href")
    val end = summary.indexOf("</a>")

    val link = summary.substring(start, end+4)
    summary.replace(link, "")
  }

  def getLink = {
    val start = summary.indexOf("<a href=")
    val end = summary.indexOf(">", start)

    val link = summary.substring(start+9, end-1)
    link
  }

}
