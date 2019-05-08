package models.artist

import models.artistImage.ArtistImage

case class ArtistFull(id: Long, name: String, mbid: String, summary: String, content: String, images: List[ArtistImage]) {

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

  def getArtistImage: Option[ArtistImage] = {
    if (images.find(x => x.size == "extralarge" || x.size == "large").size > 0)
      images.find(x => x.size == "extralarge" || x.size == "large")
    else
      {
        if (images.size > 0)
          Some(images(0))
        else
          None
      }
  }

}
