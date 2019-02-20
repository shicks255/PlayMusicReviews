package models.album

import models.ImageGetter
import models.albumImage.AlbumImage
import models.artist.Artist
import models.track.Track

case class AlbumFull(id: Long,
                     name: String,
                     year: Int,
                     artist: Artist,
                     mbid: String,
                     url: String,
                     images: List[AlbumImage],
                     tracks: List[Track]) extends ImageGetter(images)