package models.track

case class Track(albumId: Option[Long], name: String, rank: Int, duration: Int) {
  def getTrackLength = {
    if (duration > 0)
    {
      val minutes = duration / 60
      val seconds = duration % 60
      minutes + ":" + (if (seconds < 10) 0.toString+seconds else seconds)
    }
    else
      ""
  }
}
